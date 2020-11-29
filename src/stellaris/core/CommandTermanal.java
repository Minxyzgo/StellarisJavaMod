package stellaris.core;

import java.io.*;
import java.util.StringJoiner;

import mindustry.gen.*;
import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import arc.util.Log;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

public class CommandTermanal {
	ObjectMap<String, OutputStructure> input = new ObjectMap<>(256);
	Color error = Color.red, warning = Pal.missileYellow, info = Color.lightGray;
	private Table printTable;
	private int pageamount = 25;
	private PrintStream stream;
	private ByteArrayOutputStream out;
	private BaseDialog dialog = new BaseDialog("System");
	
	public CommandTermanal() {
	    init();
	}
	
	private void init() {

		input.put(
			"help",
			new OutputStructure("help", true).with(Integer.class, "page", i -> {
			    Integer in = (Integer)i;
			    if(in < 1) throw new IllegalArgumentException("Page cannot be less than 1");
			    StringBuffer buffer = new StringBuffer();
			    input.each((key, st) -> buffer.append(st.display()));
			    String[] ss = buffer.toString().split("\\n");
			    int index = Math.min(pageamount, ss.length);
			    int allpage = (int)Math.ceil(ss.length / pageamount);
			    StringJoiner join = new StringJoiner("\\n");
			    if(in > allpage) throw new IllegalArgumentException("Page cannot be biger than allpage");
			    if(in == 1) {
			        for(int lx = 0; lx < index; lx++) {
			            join.add(ss[lx]);
			        }
			        
			    } else if(in < allpage){
			        for(int lx = in * pageamount; lx < pageamount; lx++) {
			            join.add(ss[lx]);
			        }
			        
			    } else {
			        int lastPage = allpage % pageamount;
			        for(int lx = in * pageamount; lx < lastPage; lx++) {
			            join.add(ss[lx]);
			        }
			    }
			    
			    return "-----help----- page:" + in +" allpage: 1~" + allpage + "\\n" + join.toString();
			})
		);
		out = new ByteArrayOutputStream(1024);
		stream = new PrintStream(out);
		initLogDialog();
	}
	
	public void show() {
	    dialog.show();
	}

	private void initLogDialog() {

		dialog.addCloseButton();
		dialog.cont.top();
		dialog.cont.row();
		dialog.cont.pane(table -> {
			table.left();
			printTable = table;
		});
		//	dialog.cont.image().color(Pal.accent).fillX().height(3f).pad(3f);
		Events.on(MessageEvent.class, e -> {
		    printTable.add("-> " + e).left().pad(3).padLeft(6).padRight(6).color(info);
		    printTable.row();
			String msg = e.message.trim();
			String[] msg2 = msg.split(" ");
			try {
			    String[] outString = outputmsg(msg2).split("\\n");
			    for(String s : outString) {
				    printTable.add(s).left().pad(3).padLeft(6).padRight(6).color(info);
				    printTable.row();
			    }
			} catch (Exception err) {
			    Log.err(err);
			    if(err.getMessage() == null) return;
			    String[] outErr = err.getMessage().split("\\n");
			    printTable.add("Error:").left().pad(3).padLeft(6).padRight(6).color(error);
			    for(String s : outErr) {
			        printTable.add(s).left().pad(3).padLeft(6).padRight(6).color(error);
			        printTable.row();
			    }
			}
			Events.fire(new PrintEvent());
		});
		Events.on(PrintEvent.class, e -> {dialog.cont.invalidate();printTable.invalidate();});
		/*dialog.cont.add("[red]all log");
		dialog.cont.row();
		dialog.cont.pane(table -> {
			table.left();
			System.setOut(stream);
			byte[] b = out.toByteArray();
			//		ByteArrayInputStream in = new ByteArrayInputStream(b);
			//	System.setOut(old);
			//	byte[] bytes = in.readAllBytes();
			StringBuffer buffer = new StringBuffer();
			for (byte bb : b) {
				buffer.appendCodePoint(bb);
			}

			String[] msg = buffer.toString().split("\\n");
			for (String s : msg) {
				table.add("[lightgray]" + s).left().pad(3).padLeft(6).padRight(6);
				table.row();
			}
		});*/
		dialog.cont.bottom().left().defaults().fillX().pad(10f);
		dialog.cont.pane(table -> {
			TextField f = new TextField("");
			f.setStyle(Styles.areaField);
			table.left();
			table.add(f);
			table.button(Icon.wrench, () -> Events.fire(new MessageEvent(f.getText())));
		});
		
	}

	public String outputmsg(String[] msg) throws Exception {
		String ti = msg[0];
		if (input.containsKey(ti)) {
			OutputStructure st = input.get(ti);
			Seq<Object> seq = new Seq<>();

			for (int i = 1; i < msg.length; i++) {
				String x = msg[i];
				try {
					if (x.split(".").length > 1) seq.add(Float.valueOf(x));
					seq.add(Integer.valueOf(x));
				} catch (NumberFormatException nm) {
					seq.add(x);
				}
			}
			return st.run(0, seq.toArray());


		} else {
			throw new IllegalAccessException();
		}
	}

	public static class MessageEvent {
		public String message;
		public MessageEvent(String message) {
			this.message = message;
		}
	}

	public static class PrintEvent {

	}

	static class OutputStructure {
	    
	    public final String title;
		public boolean needArgument;
		private ObjectMap<String, OutputStructure> arguments = new ObjectMap<>(16);
		protected PrintStack run;
		protected Prov<String> prov;
		protected FuncArray func;
		protected Seq<ValueStack> value;

		public String run(int count, Object... obj) throws Exception {
			int baseCount = count + 1;
			if (!needArgument || obj.length <= baseCount) {
				return prov.get();
			}

			if (!(obj[baseCount] instanceof String)) {
				if (func != null) {
					return func.get(obj);
				} else {
					try {
						return run.run.get(obj[0]);
					} catch (NullPointerException e) {
						throw new IllegalArgumentException(e);
					}
				}
			} else {
				String s = (String)obj[baseCount];
				OutputStructure st = arguments.get(s);
				return st.run(baseCount, obj);
			}
		}

		public OutputStructure(String title, boolean argument) {
		    this.title = title;
			needArgument = argument;
		}

		public OutputStructure withProv(Prov<String> prov) {
			this.prov = prov;
			return this;
		}

		public OutputStructure with(Class<?> clzss, String name, Func<Object, String> func) {
			if (run != null) {
				throw new IllegalAccessError("Cannot be initialized again");
			}
			if (clzss == String.class) throw new IllegalAccessError("Cannot be String");
			run = new PrintStack(clzss, name, func);
			return this;
		}

		public OutputStructure withMore(String[] name, FuncArray func, Class<?>... type) {
			if (func != null) {
				throw new IllegalAccessError("Can only have one parameter");
			}
			Seq<String> n = new Seq<>(name);
			n.setSize(type.length);
			String[] name2 = n.toArray(String.class);
			Seq<ValueStack> seq = new Seq<>(type.length);
			for (int i = 0; i < type.length; i++) {
				seq.add(new ValueStack(name2[i], type[i]));
			}

			value = seq;
			this.func = func;
			return this;
		}
		
		public String display() {
		    StringJoiner join = new StringJoiner(" ");
		    join.add(title);
		    if(needArgument) join.add("parameter: ");
		    if(run != null) join.add(run.clzss.toGenericString() + ":" + run.name);
		    if(value != null) value.each(stack -> join.add(stack.clzss.toGenericString() + ":" + stack.name));
		    if(arguments != null) arguments.each((key, out) -> join.add(out.display() + "\\n"));
		    return join.toString();
		}

		static class PrintStack {
			Class<?> clzss;
			String name;
			Func<Object, String> run;
			protected PrintStack(Class<?> clzss, String name, Func<Object, String> run) {
				this.clzss = clzss;
				this.name = name;
				this.run = run;
			}
		}
	}

	static interface FuncArray extends Func<Object[], String> {}
	static class ValueStack {
		final Class<?> clzss;
		final String name;
		protected ValueStack(String name, Class<?> clzss) {
			this.name = name;
			this.clzss = clzss;
		}
	}
}