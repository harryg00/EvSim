// Perlin noise class
// This class is an adaptation of an open source noise generator found on GitHub
// https://gist.github.com/alksily/7a85a1898e65c936f861ee93516e397d/
// The majority of this class was not coded by myself, I do not take credit for this code.
// I have added comments in places to better understand its function, however for references it is better to check
// my documentation (3.1.1.1) or the documentation from the GitHub link above
public class PerlinNoise {
   private static double seed; // To store the seed value
   private long default_size; // The size for the noise function
   private int[] p; // Permutation array

   // Constructor with a set seed
   public PerlinNoise(double newSeed) {
      seed = newSeed;
      this.init();
   }

   // Constructor without a set seed
   public PerlinNoise() {
      this.init();
   }

   // Initialisation method to set up the permutation array
   private void init() {
      this.p = new int[512];
      int[] permutation = new int[]{151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180};
      this.default_size = 35L;

      // Duplicate the permutation to create a larger array
      for (int i = 0; i < 256; ++i) {
         this.p[256 + i] = this.p[i] = permutation[i];
      }

   }

   // Getter and Setter for the seed
   public static void setSeed(double newSeed) {
      seed = newSeed;
   }

   public static double getSeed() {
      return seed;
   }

   // Noise function for 2D coordinates.
   // EvSim uses a 2d map, therefore only needs 2d noise
   public double noise(double x, double y) {
      double value = 0.0D;
      double size = (double) this.default_size;

      double initialSize;
      for (initialSize = size; size >= 1.0D; size /= 2.0D) {
         value += this.smoothNoise(x / size, y / size, 0.0D / size) * size;
      }

      // Normalise against the initial size
      return value / initialSize;
   }

   // Function to generate smooth noise at a given 3D point (x, y, z)
   public double smoothNoise(double x, double y, double z) {
      // Apply the seed to input coordinates
      x += seed;
      y += seed;
      z += seed;

      // Calculate integer coordinates of the 3D point
      int X = (int) Math.floor(x) & 255;
      int Y = (int) Math.floor(y) & 255;
      int Z = (int) Math.floor(z) & 255;

      // Calculate fractional part of the coordinates
      x -= Math.floor(x);
      y -= Math.floor(y);
      z -= Math.floor(z);

      // Apply fade function to the fractional parts
      double u = this.fade(x);
      double v = this.fade(y);
      double w = this.fade(z);

      // Hash coordinates to get gradient vectors
      int A = this.p[X] + Y;
      int AA = this.p[A] + Z;
      int AB = this.p[A + 1] + Z;
      int B = this.p[X + 1] + Y;
      int BA = this.p[B] + Z;
      int BB = this.p[B + 1] + Z;

      // Perform tri-linear interpolation to get the final result
      return this.lerp(w, this.lerp(v, this.lerp(u, this.grad(this.p[AA], x, y, z), this.grad(this.p[BA], x - 1.0D, y, z)),
                      this.lerp(u, this.grad(this.p[AB], x, y - 1.0D, z), this.grad(this.p[BB], x - 1.0D, y - 1.0D, z))),
              this.lerp(v, this.lerp(u, this.grad(this.p[AA + 1], x, y, z - 1.0D), this.grad(this.p[BA + 1], x - 1.0D, y, z - 1.0D)),
                      this.lerp(u, this.grad(this.p[AB + 1], x, y - 1.0D, z - 1.0D), this.grad(this.p[BB + 1], x - 1.0D, y - 1.0D, z - 1.0D))));
   }

   // Fade function for smoothing interpolation
   private double fade(double t) {
      return t * t * t * (t * (t * 6.0D - 15.0D) + 10.0D);
   }

   // Linear interpolation function
   private double lerp(double t, double a, double b) {
      return a + t * (b - a);
   }

   // Gradient function to compute dot product of gradient vectors and input coordinates
   private double grad(int hash, double x, double y, double z) {
      int h = hash & 15;
      double u = h < 8 ? x : y;
      double v = h < 4 ? y : (h != 12 && h != 14 ? z : x);
      return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
   }
}