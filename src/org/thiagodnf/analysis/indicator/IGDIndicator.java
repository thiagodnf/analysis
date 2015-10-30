package org.thiagodnf.analysis.indicator;

import jmetal.core.SolutionSet;
import jmetal.qualityIndicator.QualityIndicator;

public class IGDIndicator extends Indicator{

	public IGDIndicator(QualityIndicator qi) {
		super(qi);		
	}

	@Override
	public double execute(String file, SolutionSet population) {
		return qi.getIGD(population);
	}
}