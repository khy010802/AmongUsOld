package bepo.au.missions;

import java.util.Random;

public class CustomRandom {
	
	public int random(int a, int b) {
		Random random = new Random();
        return a+random.nextInt(b-a+1);
	}
}
