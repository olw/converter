package de.tu_darmstadt.elc.olw.jbi.component.main;

import java.util.LinkedList;
import java.util.List;

import org.apache.servicemix.common.DefaultComponent;

import de.tu_darmstadt.elc.olw.jbi.component.producer.MaterialDownloader;
import de.tu_darmstadt.elc.olw.jbi.component.producer.MaterialUploader;
import de.tu_darmstadt.elc.olw.jbi.component.producer.MaterialConverter;
import de.tu_darmstadt.elc.olw.jbi.component.producer.Reporter;




public class ConverterComponent extends DefaultComponent {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	
	protected List getConfiguredEndpoints() {
		return new LinkedList(getServiceUnit().getEndpoints());
	}

	@SuppressWarnings("rawtypes")
	@Override
	
	protected Class[] getEndpointClasses() {
		return new Class[] {MaterialConverter.class,MaterialUploader.class, MaterialDownloader.class,
				Reporter.class};
	}

}
