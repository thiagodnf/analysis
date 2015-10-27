package org.thiagodnf.analysis.generator;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import org.thiagodnf.analysis.gui.window.MessageBoxWindow;
import org.thiagodnf.analysis.util.LoggerUtils;

@SuppressWarnings("rawtypes")
public abstract class Generator extends SwingWorker {
	
	protected final static Logger logger = LoggerUtils.getLogger(Generator.class.getName());
	
	protected ProgressMonitor monitor;
	
	protected int progress;
	
	protected File[] folders;
	
	protected String separator;
	
	protected List<Generator> generators;
	
	protected JFrame parent;
	
	public Generator(JFrame parent) {
		this.separator = "_";
		this.parent = parent;
	}
	
	public void run(JFrame parent, File[] folders) {
		this.folders = folders;
		
		monitor = new ProgressMonitor(parent, "Running " + this, "...", 0, 10);
		monitor.setMillisToDecideToPopup(0);
		monitor.setMillisToPopup(0);
		
		execute();		
	}
	
	public void updateMaximum(int maximum) {
		monitor.setMaximum(maximum);
		
		this.progress = 1;

		if (monitor.isCanceled()) {
			throw new IllegalArgumentException("Canceled");
		}
	}
		
	public void updateProgress(int progress){
		monitor.setProgress(progress);
		
		if (monitor.isCanceled()) {
			throw new IllegalArgumentException("Canceled");
		}
	}
	
	public void updateProgress(String note) {
		updateProgress(note, this.progress++);
	}
	
	public void updateProgress(String note, int progress){
		updateProgress(progress);
		updateNote(note);
	}
	
	public void showMessage(String note) {
		showMessage(note, 10000);
	}

	public void showMessage(String note, int maximum) {
		updateMaximum(maximum);
		updateProgress(note);
	}
	
	public void hideMessage(){
		updateMaximum(monitor.getMaximum());
	}
	
	public void updateNote(String note){
		monitor.setNote(note);
		
		logger.info(note);
		
		if (monitor.isCanceled()) {
			throw new IllegalArgumentException("Canceled");
		}
	}
	
	@Override
	protected void done() {
		if (generators.isEmpty()) {
			MessageBoxWindow.info(parent,"Done");
		}else{
			Generator generator = generators.remove(0);
			
			generator.setGenerators(generators);
			
			generator.run(parent, folders);
		}		
	}
	
	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}
	
	public void setGenerators(List<Generator> generators) {
		this.generators = generators;		
	}
	
	public abstract String toString();
}
