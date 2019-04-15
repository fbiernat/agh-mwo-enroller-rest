package com.company.enroller.persistence;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;

@Component("meetingService")
public class MeetingService {

	DatabaseConnector connector;

	public MeetingService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Meeting> getAll() {
		String hql = "FROM Meeting";
		Query query = connector.getSession().createQuery(hql);
		return query.list();
	}

	public Meeting getMeeting(long meetingId) {
		return (Meeting) connector.getSession().get(Meeting.class, meetingId);
//		Query query = connector.getSession().createQuery("FROM Meeting m WHERE m.id=" + meetingId);
//		List<Meeting> meetingList = query.list();
//		if (meetingList.size() != 0)
//			return meetingList.get(0);
//		return null;
	}
	
	public Collection<Participant> getParticipants(int meetingId) {
		Meeting meeting = this.getMeeting(meetingId);
		if (meeting == null)
			return null;
		return meeting.getParticipants();
	}

	public Participant getMeetingParticipant(long meetingId, long participantId) {
		return null;
	}

	
}
