package at.jku.isse.ecco.adapter;

import at.jku.isse.ecco.service.listener.WriteListener;
import jdk.jshell.spi.ExecutionControl;

public interface ArtifactWriter<I, O> {

	public abstract String getPluginId();

	public abstract O[] write(O base, I input);

	public abstract O[] write(I input) throws ExecutionControl.NotImplementedException;

	public void addListener(WriteListener listener);

	public void removeListener(WriteListener listener);

}
