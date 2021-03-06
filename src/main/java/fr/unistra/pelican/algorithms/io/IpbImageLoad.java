package fr.unistra.pelican.algorithms.io;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;

import fr.unistra.pelican.Algorithm;
import fr.unistra.pelican.AlgorithmException;
import fr.unistra.pelican.Image;
import fr.unistra.pelican.IntegerImage;

/**
 * Loads images in IPB/IMG format.
 * 
 * TODO : complete the processing of all metadata
 * 
 * @author
 */
public class IpbImageLoad extends Algorithm {

	/**
	 * Input parameter
	 */
	public String filename;

	/**
	 * Output parameter
	 */
	public Image output;

	// private FileInputStream fis;
	private InputStream is;

	private LineNumberReader ih;

	private int xDim, yDim, zDim;

	private int bytesPerPixel;

	private boolean isUnsigned = false;

	private boolean isBigEndian;

	private final int bufferSize = 4096;

	/**
	 * Constructor
	 * 
	 */
	public IpbImageLoad() {

		super();
		super.inputs = "filename";
		super.outputs = "output";
		
	}

	public void launch() throws AlgorithmException {
		// Test if the input file is compressed or not
		try {
			is = new DataInputStream(new FileInputStream(filename + ".img"));
			is = new BufferedInputStream(is);
			ih = new LineNumberReader(new FileReader(filename + ".ipb"));
			// Parse the header
			parseHeader();
			// Generate output
			output = new IntegerImage(xDim, yDim, zDim, 1, 1);

			// Parse the data
			parseRawData();
			// Close the file
			is.close();
		} catch (IOException ex) {
			throw new AlgorithmException("file reading error with file: "
					+ filename);
		}
	}

	private void parseLine(String line) {
		String[] s = line.split("=", 2);

		if (s.length == 2) {
			String rightToken = s[0];
			String leftToken = s[1].trim();
			if (rightToken.compareTo("width") == 0)
				xDim = Integer.parseInt(leftToken);
			if (rightToken.compareTo("height") == 0)
				yDim = Integer.parseInt(leftToken);
			if (rightToken.compareTo("depth") == 0)
				zDim = Integer.parseInt(leftToken);
			if (rightToken.compareTo("bits per pixel") == 0)
				bytesPerPixel = Integer.parseInt(leftToken) / 8;
			if (rightToken.compareTo("endian") == 0)
				isBigEndian = (leftToken.compareTo("littleEndian") != 0);
		}
	}

	/*
	 * Private methods
	 */
	private void parseHeader() throws IOException {
		String line = ih.readLine();
		while (line != null) {
			parseLine(line);
			line = ih.readLine();
		}
	}

	private void printHeader() {
		System.out.println("xDim          : " + xDim);
		System.out.println("yDim          : " + yDim);
		System.out.println("zDim          : " + zDim);
		System.out.println("bytesPerPixel : " + bytesPerPixel);
		System.out.println("isBigEndian   : " + isBigEndian);
	}

	private void parseRawData() throws IOException {
		int value;
		int numPixelRead = 0;
		byte[] buffer = new byte[bufferSize];
		;
		int numPixelPerBuffer = bufferSize / bytesPerPixel;
		is.read(buffer, 0, bufferSize);
		for (int z = 0; z < zDim; z++)
			for (int y = 0; y < yDim; y++)
				for (int x = 0; x < xDim; x++) {
					value = 0;
					for (int i = 0; i < bytesPerPixel; i++)
						value += (buffer[numPixelRead * bytesPerPixel
								+ (isBigEndian ? (bytesPerPixel - 1 - i) : i)] & 0x000000FF) << (i * 8);
					numPixelRead++;
					value = isUnsigned ? value
							: ((value >> (bytesPerPixel * 8 - 1)) * 0xFFFF0000)
									| value;
					// System.out.print(" "+value);
					output.setPixelXYZInt(x, y, z, value);
					if (numPixelRead == numPixelPerBuffer) {
						is.read(buffer, 0, bufferSize);
						numPixelRead = 0;
					}
				}
	}

	/**
	 * Loads images in IPB/IMG format.
	 * 
	 * @param filename
	 *            Filename of the PB/IMG image.
	 * @return The PB/IMG image.
	 */
	public static Image exec(String filename) {
		return (Image) new IpbImageLoad().process(filename);
	}

}
