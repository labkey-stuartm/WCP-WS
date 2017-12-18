/**
 * 
 */
package com.studymetadata.bean;

import com.studymetadata.util.StudyMetaDataConstants;

/**
 * @author Mohan
 * @createdOn Nov 3, 2017 12:22:36 PM
 *
 */
public class FetalKickCounterFormatBean {
	
	private Integer duration = Integer.parseInt(StudyMetaDataConstants.FETAL_MAX_DURATION);
	private Integer kickCount = StudyMetaDataConstants.MAX_KICK_COUNT;
	
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	public Integer getKickCount() {
		return kickCount;
	}
	public void setKickCount(Integer kickCount) {
		this.kickCount = kickCount;
	}
	
}
