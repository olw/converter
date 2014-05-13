/**
 * 
 */
package de.tu_darmstadt.elc.olw.api.media;

import java.io.File;
import java.io.IOException;


public interface XMLFlash {
	public void createXMLFlash (File xmlFile) throws IOException;
	public void setLectureURL(String lectureURL);
	public void setSlidesVideoURL(String slidesVideoURL);
	public void setStreamURL(String streamURL);
}
