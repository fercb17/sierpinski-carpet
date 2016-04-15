package fercb17;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SierpinskiCarpet {

	private static byte[][] msa = null;
	
	public static void main(String[] args) {
		int maxDepth = 4;
		
		System.out.println("Making Menger sponge...");
		
		try {
			msa = setup(maxDepth);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(msa != null) {
			Coord origin = new Coord(0, 0);
			Coord offset = new Coord(msa.length, msa.length);
			generateMengerSponge(
					0												//depth
					, 0												//sector
					, new Constraint(								//Constraint
							origin									//a
							, Coord.offset(origin, offset, 1, 0)	//b or right top
							, Coord.offset(origin, offset, 0, 1)	//c or left bottom
							)
					, 4												//max depth
					);
			export(msa.length, "testimage");
		}
		
		System.out.println("Completed Menger sponge.");
		
//		for(int i = 0; i < msa.length; i++) {
//			for(int j = 0; j < msa.length; j++) {
//				System.out.print(msa[j][i] + " ");
//			}
//			System.out.print("\n");
//		}
	}
	
	public static byte[][] setup(int depth) {
		
		if(depth > 4) {
			System.out.println("Depth cannot exceed 4 if not resolution of image required exceeds 43m with a pixel count of 2 x 10^15");
			return null;
		}
		
		int s = (int) Math.sqrt(Math.pow(3, Math.pow(2, depth)));
		return new byte[s][s];
	}
	
	public static void generateMengerSponge(int depth, int sector, Constraint ct, int mdp) {
		if(depth > mdp || mdp == 9) {
			return;
		} else { //depth < max depth
			for(int i = 0; i < 9; i++) {
				if (i != 4) {
					generateMengerSponge(depth+1, i, Constraint.calculate(i, ct.a, ct.b, ct.c), mdp);
					if(depth == mdp) {
						fill(ct);
					}
				}
			}
		}
	}
	
	public static void fill(Constraint ct) {
		for(int i = ct.a.x; i < ct.b.x; i++) {
			for(int j = ct.a.y; j < ct.c.y; j++) {
				msa[i][j] = 1;
			}
		}
		return;
	}
	
	public static void export(int arrayRes, String imageName) {
		BufferedImage image = new BufferedImage(arrayRes, arrayRes, BufferedImage.TYPE_INT_ARGB);
		File f = null;
		
		try {			
			//apply menger square to image
			for(int i = 0; i < arrayRes; i++) {
				for(int j = 0; j < arrayRes; j++) {
					int c = Color.BLACK.getRGB();
					if(msa[i][j] == 1) {
						c = Color.WHITE.getRGB();
					}
					
					image.setRGB(i, j, c);
				}
			}
			
			f = new File("G://" + imageName + ".png");
			ImageIO.write(image, "png", f);
		} catch (IOException e) {
			System.out.println("--- Export Error ---\n" + e.toString());
		}
	}

}

class Constraint {
	public Coord a, b, c;
	
	@SuppressWarnings("unused")	private Constraint() {}
	
	public Constraint(Coord a, Coord b, Coord c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public static Constraint calculate(int sector, Coord a, Coord b, Coord c) {
		
		Coord offset = new Coord((b.x - a.x) / 3, (c.y - a.y) / 3);
		Coord start = new Coord(a.x, a.y);
		
		if(sector == 0 || sector == 1 || sector == 2) {
			start.x += offset.x * sector;
		} else if(sector == 3 || sector == 5) {
			start.x += offset.x * (sector - 3);
			start.y += offset.y;
		} else if(sector == 6 || sector == 7 || sector == 8) {
			start.x += offset.x * (sector - 6);
			start.y += offset.y * 2;
		}
		
		return new Constraint(
				Coord.offset(start, offset, 0, 0)	//a
				, Coord.offset(start, offset, 1, 0)	//b
				, Coord.offset(start, offset, 0, 1)	//c
				);
		//return new Constraint(Coord.offset(start, offset), new Coord(start.x + offset.x, start.y), new Coord(start.x, start.y + offset.y));
	}
}

class Coord {
	int x, y = 0;
	
	@SuppressWarnings("unused")	private Coord() {}
	public Coord(int x, int y) {this.x = x; this.y = y; }
	
	public static Coord offset(Coord a, Coord b) {
		return new Coord(a.x + b.x, a.y + b.y);
	}
	
	public static Coord offset(Coord a, Coord b, int x, int y) {
		Coord offset = new Coord(0,0);
		if(x > 0)
			offset.x = b.x;
		if(y > 0)
			offset.y = b.y;
		
		return new Coord(a.x + offset.x, a.y + offset.y);
	}
}