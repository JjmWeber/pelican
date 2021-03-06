package fr.unistra.pelican.algorithms.segmentation.qfz.gray;

import java.util.ArrayList;

import fr.unistra.pelican.Algorithm;
import fr.unistra.pelican.AlgorithmException;
import fr.unistra.pelican.Image;
import fr.unistra.pelican.IntegerImage;
import fr.unistra.pelican.algorithms.conversion.AverageChannels;
import fr.unistra.pelican.algorithms.conversion.RGBToGray;
import fr.unistra.pelican.util.Point4D;


/**
 * Compute connected components of the image according to alpha connectivity defined by local range alpha.
 * 
 * Alpha value is used in byte precision.
 * 
 * Only deal with Gray Levels, non-gray images will be transformed. X,Y,Z,T dimensions are taken into account.
 * 
 * Algorithm is mine and quick made, probably better implementation exists but this one is quite fast.
 * 
 * @author Jonathan Weber
 *
 */
public class GrayAlphaConnectivityCC extends Algorithm {

	/**
	 * Image to process
	 */
	public Image inputImage;
	
	/**
	 * alpha value for local range
	 */
	public int alpha;
	
	/**
	 * Connectivity used to determine the flat zones
	 */
	public Point4D[] neighbourhood;;
	
	/**
	 * Flat Zones labels
	 */
	public IntegerImage outputImage;
	
	private static final int INITVALUE = -2;
	private static final int INQUEUE = -1;
	
	private ArrayList<Point4D> neighboursToExpand;
	
	private int xDim;
	private int yDim;
	private int zDim;
	private int tDim;
	
	private int currentLabel;

	
	/**
	 * Constructor
	 * 
	 */
	public GrayAlphaConnectivityCC() 
	{
		super();
		super.inputs = "inputImage,alpha,neighbourhood";
		super.outputs = "outputImage";
	}
	
	@Override
	public void launch() throws AlgorithmException {
		
		xDim = inputImage.getXDim();
		yDim = inputImage.getYDim();
		zDim = inputImage.getZDim();
		tDim = inputImage.getTDim();
		
		neighboursToExpand = new ArrayList<Point4D>();		
		
		outputImage= new IntegerImage(xDim,yDim,zDim,tDim,1);
		outputImage.fill(INITVALUE);
		// Transforming image in grey level if not
		if(inputImage.getBDim()==3)
		{
			inputImage = RGBToGray.exec(inputImage);
		} 
		else if(inputImage.getBDim()!=1)
		{
			inputImage = AverageChannels.exec(inputImage);
		}
		
		currentLabel=-1;
		
		for(int t=tDim;--t>=0;)
			for(int z=zDim;--z>=0;)
				for(int y=yDim;--y>=0;)
					for(int x=xDim;--x>=0;)
					{
						if(outputImage.getPixelXYZTInt(x, y, z, t)==INITVALUE)
						{
							outputImage.setPixelXYZTInt(x, y, z, t, ++currentLabel);
							addUnlabelledNeighboursRespectToKValueToQueue(x, y, z, t);
							while(neighboursToExpand.size()!=0)
							{
								expandCurrentLabelTo(neighboursToExpand.remove(0));
							}
						}
					}		
	}
	
	private final void expandCurrentLabelTo(Point4D pixel)
	{
				outputImage.setPixelXYZTInt(pixel.x, pixel.y, pixel.z, pixel.t, currentLabel);
				addUnlabelledNeighboursRespectToKValueToQueue(pixel.x, pixel.y, pixel.z, pixel.t);		
	}
	
	private final void addUnlabelledNeighboursRespectToKValueToQueue(int x, int y, int z, int t)
	{
		int pixelValue = inputImage.getPixelXYZTByte(x, y, z, t);
		for(int i=neighbourhood.length;--i>=0;)
		{
			int locX = x + neighbourhood[i].x;
			int locY = y + neighbourhood[i].y;
			int locZ = z + neighbourhood[i].z;
			int locT = t + neighbourhood[i].t;
			if(locX>=0&&locY>=0&&locZ>=0&&locT>=0&&locX<xDim&&locY<yDim&&locZ<zDim&&locT<tDim)
			{
				if(outputImage.getPixelXYZTInt(locX, locY, locZ, locT)==INITVALUE)
				{
					if(Math.abs(pixelValue-inputImage.getPixelXYZTByte(locX, locY, locZ, locT))<=alpha)
					{
						neighboursToExpand.add(new Point4D(locX, locY, locZ, locT));
						outputImage.setPixelXYZTInt(locX, locY, locZ, locT,INQUEUE);
					}
				}
			}
		}	
	}
	
	public static IntegerImage exec(Image inputImage, int alpha, Point4D[] neighbourhood) 
	{
		return (IntegerImage)new GrayAlphaConnectivityCC().process(inputImage,alpha,neighbourhood);
	}
}
