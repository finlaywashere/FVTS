package ca.team2706.fvts.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;

import ca.team2706.fvts.core.data.VisionData;
import ca.team2706.fvts.core.image.AbstractImagePreprocessor;
import ca.team2706.fvts.core.input.AbstractInputDevice;
import ca.team2706.fvts.core.interfaces.AbstractInterface;
import ca.team2706.fvts.core.math.AbstractMathProcessor;
import ca.team2706.fvts.core.params.VisionParams;
import ca.team2706.fvts.core.pipelines.AbstractPipeline;
import ca.team2706.fvts.main.Main;

public class MainThread extends Thread {

	public VisionParams visionParams;
	public ParamsSelector selector;
	public boolean error = false;
	public boolean stop = false;

	public MainThread(VisionParams params) {
		this.visionParams = params;
		String interfaceN = visionParams.getByName("core/interface").getValue();
		String[] interfaces = interfaceN.split(",");
		outputInterface = new ArrayList<AbstractInterface>();
		for(String s : interfaces) {
			AbstractInterface i = AbstractInterface.getByName(interfaceN);
			if (i == null) {
				Log.e("Interface "+s+" does not exist in profile " + visionParams.getByName("name").getValue(), true);
				System.exit(1);
			}
			i.init(this);
			outputInterface.add(i);
		}

		String pipelineN = visionParams.getByName("core/pipeline").getValue();
		pipeline = AbstractPipeline.getByName(pipelineN);
		if (pipeline == null) {
			Log.e("No pipeline found for profile " + visionParams.getByName("name").getValue(), true);
			System.exit(1);
		}
		pipeline.init(this);

		this.maths = new ArrayList<AbstractMathProcessor>();
		if (visionParams.getByName("core/maths") != null) {
			String mathNames = visionParams.getByName("core/maths").getValue();
			String[] maths = mathNames.split(",");
			for (String math : maths) {
				AbstractMathProcessor processor = AbstractMathProcessor.getByName(math);
				if (processor == null) {
					Log.e("No math processor found for profile " + visionParams.getByName("name").getValue()
							+ " by the name of " + math, true);
					System.exit(1);
				}
				processor.init(this);
				this.maths.add(processor);
			}
		}

		this.processors = new ArrayList<AbstractImagePreprocessor>();
		if (visionParams.getByName("core/preprocessors") != null) {
			String preProcessorNames = visionParams.getByName("core/preprocessors").getValue();
			String[] processors = preProcessorNames.split(",");
			for (String p : processors) {
				AbstractImagePreprocessor processor = AbstractImagePreprocessor.getByName(p);
				if (processor == null) {
					Log.e("No image preprocessor found for profile " + visionParams.getByName("name").getValue()
							+ " by the name of " + p, true);
					System.exit(1);
				}
				processor.init(this);
				this.processors.add(processor);
			}
		}
	}

	public VisionParams getVisionParams() {
		return visionParams;
	}

	public void setOutputInterface(List<AbstractInterface> outputInterface) {
		this.outputInterface = outputInterface;
	}

	public String getParamName() {
		return visionParams.getByName("name").getValue();
	}
	
	public Mat frame;
	public boolean useCamera;
	public static int timestamp = 0;
	public double lastDist = 0;
	private List<AbstractInterface> outputInterface;
	private AbstractPipeline pipeline;
	private List<AbstractMathProcessor> maths;
	private List<AbstractImagePreprocessor> processors;
	public boolean overrideCamera = false;

	@Override
	public void run() {
		// Initializes a Matrix to hold the frame

		frame = new Mat();

		AbstractInputDevice input = AbstractInputDevice.getByName(visionParams.getByName("core/type").getValue());
		useCamera = !input.isStaticFrame() | overrideCamera;
		try {
			VisionCameraServer.initCamera(visionParams.getByName("core/type").getValue(),
					visionParams.getByName("core/identifier").getValue());
			VisionCameraServer.update();
		} catch (Exception e) {
			Log.e(e.getMessage(), true);
		}
		// The window to display the raw image
		DisplayGui guiRawImg = null;
		// The window to display the processed image
		DisplayGui guiProcessedImg = null;
		// Wether to open the guis
		boolean use_GUI = Main.developmentMode;

		frame = VisionCameraServer.getFrame(visionParams.getByName("core/type").getValue(),
				visionParams.getByName("core/identifier").getValue());

		// Set up the GUI display windows
		if (use_GUI) {
			// Initializes the window to display the raw image
			guiRawImg = new DisplayGui(1, 1, "Raw-" + visionParams.getByName("name").getValue(), true);
			// Initializes the window to display the processed image
			guiProcessedImg = new DisplayGui(1, 1, "Processed-" + visionParams.getByName("name").getValue(), true);
			// Initializes the parameters selector window
			try {
				selector = new ParamsSelector(visionParams,this);
			} catch (Exception e) {
				Log.e(e.getMessage(), true);
				e.printStackTrace();
				error = true;
			}
		}

		Log.i("Initialized profile " + visionParams.getByName("name").getValue(), true);

		// Main video processing loop
		while (true) {
			if(stop)
				break;
			try {

				if (!visionParams.getByName("enabled").getValueB() && use_GUI) {

					guiRawImg.b = false;
					guiProcessedImg.b = false;

					guiRawImg.dispose();
					guiProcessedImg.dispose();

					break;

				} else if (!visionParams.getByName("enabled").getValueB()) {
					break;
				}

				// Read the frame
				frame = VisionCameraServer.getFrame(visionParams.getByName("core/type").getValue(),
						visionParams.getByName("core/identifier").getValue()).clone();
				if (useCamera) {
					for (AbstractImagePreprocessor processor : processors) {
						Mat newFrame = null;
						try {
							newFrame = processor.process(frame, this);
						}catch(Exception e) {
							e.printStackTrace();
							error = true;
						}
						frame.release();
						frame = newFrame;
					}
				}

				// Process the frame!
				// Log when the pipeline starts
				long pipelineStart = System.nanoTime();
				// Process the frame
				VisionData visionData = pipeline.process(frame, visionParams);
				// Log when the pipeline stops
				long pipelineEnd = System.nanoTime();
				for (AbstractMathProcessor processor : maths) {
					processor.process(visionData, this);
				}
				// Creates the raw output image object
				Mat rawOutputImg;
				if (use_GUI) {
					// If use gui then draw the prefered target
					// Sets the raw image to the frame
					rawOutputImg = frame.clone();

					// Draws the preffered target
					pipeline.drawPreferredTarget(rawOutputImg, visionData);
				} else {
					// Sets the raw image to the frame
					rawOutputImg = frame.clone();
				}

				if (visionData.preferredTarget != null)
					lastDist = (Double) visionData.preferredTarget.data.get("distance");
				for(AbstractInterface inf : outputInterface) {
					inf.publishData(visionData, this);
				}

				// display the processed frame in the GUI
				if (use_GUI) {
					try {
						// May throw a NullPointerException if initializing
						// the window failed
						BufferedImage raw = Utils.matToBufferedImage(rawOutputImg);
						if (visionData.preferredTarget != null) {
							double dist = (Double) visionData.preferredTarget.data.get("distance");
							Graphics g = raw.createGraphics();
							g.setColor(Color.GREEN);
							g.drawString("dist: " + dist, 50, 50);
							g.dispose();
						}
						guiRawImg.updateImage(raw);
						guiProcessedImg.updateImage(Utils.matToBufferedImage(visionData.binMask.clone()));
					} catch (IOException e) {
						// means mat2BufferedImage broke
						// non-fatal error, let the program continue
						Log.e(e.getMessage(), true);
						error = true;
						continue;
					} catch (NullPointerException e) {
						Log.e(e.getMessage(), true);
						Log.i("Window closed", true);
						error = true;
						Runtime.getRuntime().halt(0);
					} catch (Exception e) {
						// just in case
						Log.e(e.getMessage(), true);
						error = true;
						continue;
					}
				}

				// Display the frame rate onto the console
				double pipelineTime = (((double) (pipelineEnd - pipelineStart))
						/ Constants.NANOSECONDS_PER_SECOND) * 1000;
				Log.i("Vision FPS: " + visionData.fps + ", pipeline took: " + pipelineTime + " ms\n", false);
			} catch (Exception e) {
				Log.e(e.getMessage(), true);
				e.printStackTrace();
				error = true;
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					Log.e(e1.getMessage(), true);
					e.printStackTrace();
					error = true;
				}
			}
		}
		if(use_GUI) {
			guiProcessedImg.close();
			guiRawImg.close();
		}
	}

	public void updateParams(VisionParams params) {
		this.visionParams = params;
	}

}
