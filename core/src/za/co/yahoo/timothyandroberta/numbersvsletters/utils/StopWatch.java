package za.co.yahoo.timothyandroberta.numbersvsletters.utils;

public class StopWatch {
	public float currentTime;
	public float period;

	public boolean isFinished() {
		return currentTime <= 0;
	}

	public void update(float deltaTime) {
		if (!isFinished())
			currentTime -= deltaTime;
	}

	public void reset() {
		currentTime = period;
	}

	public StopWatch(float period) {
		this.period = period;
		this.currentTime = period;
	}

	public StopWatch() {
	}

	public void setPeriod(float period) {
		this.period = period;
	}
}
