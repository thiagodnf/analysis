package org.thiagodnf.analysis.task.generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JFrame;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;

import org.apache.commons.io.FilenameUtils;
import org.thiagodnf.analysis.indicator.Indicator;
import org.thiagodnf.analysis.util.IndicatorUtils;
import org.thiagodnf.analysis.util.LoggerUtils;
import org.thiagodnf.analysis.util.SolutionSetUtils;
import org.thiagodnf.core.util.PropertiesUtils;

public class QualityIndicatorsGenerator extends Generator {
	
	protected final static Logger logger = LoggerUtils.getLogger(QualityIndicatorsGenerator.class.getName());
	
	public QualityIndicatorsGenerator(JFrame frame, File[] folders) {
		super(frame, folders);
	}
	
	protected Void doInBackground() throws Exception {

		generateFor("FUN_", 2);
		generateFor("FUNALL", 2);
		generateFor("PFKNOWN", 1);

		return null;
	}
	
	protected void generateFor(String startWith, int level) throws Exception{
		List<String> files = getFilesStartingWith(folders, startWith);
		
		logger.info("Sorting files");
		
		Map<String,List<String>> map = new HashMap<String, List<String>>();		
		
		for (String filename : files) {

			String key = new File(filename).getParentFile().getAbsolutePath();

			if (!map.containsKey(key)) {
				map.put(key, new ArrayList<String>());
			}

			map.get(key).add(filename);
		}
		
		logger.info("Files were sorted");

		updateMaximum(files.size());
		
		for (Entry<String, List<String>> entry : map.entrySet()) {
			generate(entry.getKey(), entry.getValue(), files.size(), level);
		}
		
		afterFinishing();
		
		logger.info("Done");
	}
	
	protected void generate(String folder, List<String> files, int totalOfFiles, int level) throws Exception {
		logger.info("Generating Quality Indicators for " + folder+". Finding a True Pareto-front file in the path");
		
		File parent = new File(folder);

		for (int i = 0; i < level; i++) {
			parent = parent.getParentFile();
		}
		
		String approxTrueParetoFrontPath = parent.getAbsolutePath() + File.separator+"PFAPPROX";
		
		if (!new File(approxTrueParetoFrontPath).exists()) {
			throw new Exception("You must to define a true Pareto-front before");
		}
		
		SolutionSet paretoFront = SolutionSetUtils.getFromFile(approxTrueParetoFrontPath);
		
		if (paretoFront.size() == 0) {
			throw new IllegalArgumentException("The Approximate Pareto-front cannot be empty");
		}
		
		int numberOfObjectives = paretoFront.get(0).getNumberOfObjectives();

		Problem p = new FakeProblem(numberOfObjectives);
		
		QualityIndicator qi = new QualityIndicator(p, approxTrueParetoFrontPath);
		
		for (String file : files) {
			generateQualityIndicators(qi, folder, file, paretoFront, totalOfFiles);
		}
	}
		
	protected void generateQualityIndicators(QualityIndicator qi, String folder, String file,SolutionSet paretoFront, int totalOfFiles) throws IOException{
		updateNote(getCurrentProgress() + " from " + totalOfFiles);
		
		logger.info("Generating QI for file " + file);
		
		SolutionSet population = SolutionSetUtils.getFromFile(file);
		
		Properties values = new Properties();
		
		List<Indicator> indicators = IndicatorUtils.getIndicatorList();

		for (Indicator ind : indicators) {
			values.put(ind.getKey(),ind.execute(qi, paretoFront, file, population));
		}
		
		values = convertAllValuesToString(values);
		
		String filename = FilenameUtils.getBaseName(file);

		String outputFile = folder + File.separator+"QI_" + filename;

		PropertiesUtils.save(new File(outputFile), values);

		updateProgress();
	}
	
	protected Properties convertAllValuesToString(Properties prop){
		for (Object key : prop.keySet()) {
			prop.put(key, String.valueOf(prop.get(key)));
		}
		
		return prop;
	}
	
	protected class FakeProblem extends Problem{
		
		private static final long serialVersionUID = -1352896728518075561L;

		public FakeProblem(int numberOfObjectives){
			super.numberOfVariables_ = 1;
			super.numberOfObjectives_ = numberOfObjectives;
			super.numberOfConstraints_ = 0;
			super.problemName_ = FakeProblem.class.getCanonicalName();
			super.solutionType_ = new BinarySolutionType(this);
			super.length_ = new int[numberOfVariables_];
		}

		@Override
		public void evaluate(Solution arg0) throws JMException { }		
	}
	
	@Override
	public String toString() {
		return "Running Quality Indicators Generator";
	}	
}

