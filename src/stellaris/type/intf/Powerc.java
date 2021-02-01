package stellaris.type.intf;

import arc.util.Time;

public interface Powerc {
	float powerc();
	float maxPower();
	float status();
	void status(float value);
	boolean conPower(float value);
	default void trigger(float value) {
	    status(Math.max(status() - value * Time.delta, 0f));
	}
}