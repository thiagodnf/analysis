package org.thiagodnf.analysis.indicator;

import jmetal.core.SolutionSet;
import jmetal.qualityIndicator.QualityIndicator;

import org.thiagodnf.analysis.util.SolutionSetUtils;

public class NumberOfNonRepeatedSolutionsIndicator extends Indicator {

	public NumberOfNonRepeatedSolutionsIndicator() {
		super("Non-repeated Solutions", "non-repeated-solutions");
	}

	@Override
	public double execute(QualityIndicator qi, SolutionSet paretoFront,String file, SolutionSet population) {
		SolutionSet nonRepeatedpopulation = SolutionSetUtils.removeRepeatedSolutions(population);
		return nonRepeatedpopulation.size();
	}
}
