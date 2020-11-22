package stellaris.type.intf;

public interface Powerc {
	float powerc();
	float maxPower();
	float status();
	void status(float value);
	boolean conPower(float value);
}