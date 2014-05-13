package de.tu_darmstadt.elc.olw.jbi.component.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.Logger;

public class ConverterRoute extends RouteBuilder {
	private static Logger logger = Logger.getLogger(ConverterRoute.class);
	private String activeMQQueue;

	/**
	 * @param activeMQQueue
	 *            the activeMQQueue to set
	 */
	public void setActiveMQQueue(String activeMQQueue) {
		this.activeMQQueue = activeMQQueue;
	}

	/**
	 * @return the activeMQQueue
	 */
	public String getActiveMQQueue() {
		return activeMQQueue;
	}

	public ConverterRoute() {
		logger.info("Constructing converter component .....");
		activeMQQueue = "";
	}

	@SuppressWarnings("unused")
	private Predicate isType(String mimeType) {
		Predicate condition = header("type").isEqualTo((String) mimeType);
		return condition;
	}

	@Override
	public void configure() throws Exception {
		logger.info("Constructing converter component ....");

		from(activeMQQueue)
				.process(new MessageRouter())

				.to("jbi:service:http://olw.elc.tu-darmstadt.de/services/downloadService001?mep=in-out")

				.to("jbi:service:http://olw.elc.tu-darmstadt.de/services/converterService001?mep=in-out")

				.to("jbi:service:http://olw.elc.tu-darmstadt.de/services/uploadService001?mep=in-out")

				.to("jbi:service:http://olw.elc.tu-darmstadt.de/services/reporterService001?mep=in-out");

	}

	public static class MessageRouter implements Processor {
		@Override
		public void process(Exchange exchange) {
			try {
			String in = exchange.getIn().getBody(String.class);
			if (in == null)
				return;
			logger.info("[Camel] :" + in);
			} catch (Exception e) {
				logger.error("[Camel] :"  + e.getLocalizedMessage());
			}
			

		}
		

	}

}
