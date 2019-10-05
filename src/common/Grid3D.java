package common;

public class Grid3D<T> {//probably won't be used

	Object[] data;
	
	int sizeX, sizeY, sizeZ;

	public Grid3D(int i, int j, int k) {
		sizeX = i;
		sizeY = j;
		sizeZ = k;
		data = new Object[i*j*k];
	}
	
	public Grid3D(int i) {
		sizeX = i;
		sizeY = i;
		sizeZ = i;
	}
	
	public T get(int i, int j, int k)
	{
		return (T) data[indexFromCoords(i,j,k)];
	}
	
	public void set(T value, int i, int j, int k)
	{
		data[indexFromCoords(i,j,k)] = value;
	}
	
	public int indexFromCoords(int i, int j, int k)
	{
		if(checkBounds(i,0,sizeX) && checkBounds(j,0,sizeY) && checkBounds(k,0,sizeY))
		{
			int yOffset = j*sizeX;
			int zOffset = k*sizeX*sizeY;
			return i + yOffset + zOffset;
		} else
		{
			System.out.println("Out of Bounds: " + i + '/' + sizeX + " " +  j + '/' + sizeY + " " +  k + '/' + sizeZ);
			return -1;
		}
		
	}
	
	public int[] coordsFromIndex(int i)
	{
		int[] coords = new int[3];
		int index = i;
		coords[0] = index%(sizeX);
		coords[1] = i/sizeX;
		coords[2] = i/(sizeX*sizeY);
 		
		return coords;
	}
	
	private boolean checkBounds(int i, int min, int max) // [min,max)
	{
		if(i < min) return false;
		if(i >= max) return false;
		return true;
	}
	
	

}
