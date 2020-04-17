package ca.team2706.fvts.modules;

import java.io.File;

import ca.team2706.fvts.core.image.AbstractImagePreprocessor;
import ca.team2706.fvts.core.input.AbstractInputDevice;
import ca.team2706.fvts.core.interfaces.AbstractInterface;
import ca.team2706.fvts.core.math.AbstractMathProcessor;
import ca.team2706.fvts.core.pipelines.AbstractPipeline;

public abstract class Module {
	public abstract void init(File resourcesFolder);
	
	protected void registerImagePreprocessor(AbstractImagePreprocessor processor) {
		AbstractImagePreprocessor.imageProcessors.add(processor);
	}
	protected void registerInputDevice(AbstractInputDevice input) {
		AbstractInputDevice.inputs.add(input);
	}
	protected void registerInterface(AbstractInterface aInterface) {
		AbstractInterface.interfaces.add(aInterface);
	}
	protected void registerMathProcessor(AbstractMathProcessor math) {
		AbstractMathProcessor.maths.add(math);
	}
	protected void registerPipeline(AbstractPipeline pipeline) {
		AbstractPipeline.pipelines.add(pipeline);
	}
}
