package com.company.enroller.persistence;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.Transaction;
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
//		String hql = "FROM Meeting";
//		Query query = connector.getSession().createQuery(hql);
//		return query.list();
		return connector.getSession().createCriteria(Meeting.class).list();
	}

	public Meeting getMeeting(long meetingId) {
		return (Meeting) connector.getSession().get(Meeting.class, meetingId);
//		Query query = connector.getSession().createQuery("FROM Meeting m WHERE m.id=" + meetingId);
//		List<Meeting> meetingList = query.list();
//		if (meetingList.size() != 0)
//			return meetingList.get(0);
//		return null;
	}
	
	public Collection<Participant> getParticipants(long meetingId) {
		Meeting meeting = this.getMeeting(meetingId);
		if (meeting == null)
			return null;
		return meeting.getParticipants();
	}

	public void add(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().save(meeting);
		transaction.commit();
	}

	public void addParticipant(long meetingId, Participant newParticipant) {
		Transaction transaction = connector.getSession().beginTransaction();
		Meeting meeting = this.getMeeting(meetingId);
		meeting.addParticipant(newParticipant);
		connector.getSession().save(meeting);
		connector.getSession().save(newParticipant);
		transaction.commit();
	}
	
	public void update(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().update(meeting);
		transaction.commit();
	}
	
}
