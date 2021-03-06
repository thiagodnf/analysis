package org.thiagodnf.analysis.indicator;

import jmetal.core.SolutionSet;
import jmetal.qualityIndicator.QualityIndicator;
import org.thiagodnf.analysis.util.SolutionSetUtils;

public class InParetoFrontIndicator extends Indicator {

	public InParetoFrontIndicator(){
		super("In Pareto-front", "in-pareto-front");
	}

	@Override
	public double execute(QualityIndicator qi, SolutionSet paretoFront, String file, SolutionSet population) {
		SolutionSet nonRepeatedpopulation = SolutionSetUtils.removeRepeatedSolutions(population);
		
		SolutionSet intersec = SolutionSetUtils.getIntersection(paretoFront, nonRepeatedpopulation);
		
		return intersec.size();
	}
}
