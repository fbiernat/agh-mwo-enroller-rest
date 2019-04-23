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
		return connector.getSession().createCriteria(Meeting.class).list();
	}

	public Meeting get(long meetingId) {
		return (Meeting) connector.getSession().get(Meeting.class, meetingId);
	}

	public Collection<Participant> getParticipants(long meetingId) {
		Meeting meeting = this.get(meetingId);
		if (meeting == null)
			return null;
		return meeting.getParticipants();
	}

	public Meeting add(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().save(meeting);
		transaction.commit();
		return meeting;
	}

	public Participant addParticipant(long meetingId, Participant newParticipant) {
		Transaction transaction = connector.getSession().beginTransaction();
		Meeting meeting = this.get(meetingId);
		meeting.addParticipant(newParticipant);
		connector.getSession().save(meeting);
		connector.getSession().save(newParticipant);
		transaction.commit();
		return newParticipant;
	}

	public Meeting update(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().update(meeting);
		transaction.commit();
		return meeting;
	}

	public Meeting delete(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().delete(meeting);
		transaction.commit();
		return meeting;
	}

	public Collection<Meeting> getSortedByTitle() {
		String hql = "FROM Meeting ORDER BY lower(title) asc";
		Query query = connector.getSession().createQuery(hql);
		return query.list();
	}

	public Collection<Meeting> searchTitleAndDesc(String phrase) {
		String hql = "FROM Meeting m WHERE (m.title LIKE ? OR m.description LIKE ?)";
		Query query = connector.getSession().createQuery(hql);
		query.setParameter(0, "%" + phrase + "%");
		query.setParameter(1, "%" + phrase + "%");
		return query.list();
	}

	public Collection<Meeting> searchByParticipant(String participant) {
		String hql = "SELECT m FROM Meeting m JOIN m.participants p WHERE p.login LIKE ?";
		Query query = connector.getSession().createQuery(hql);
		query.setParameter(0, participant);
		return query.list();
	}

}
