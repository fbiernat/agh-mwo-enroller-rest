package com.company.enroller.persistence;

import java.util.Collection;

import org.springframework.stereotype.Component;

import com.company.enroller.model.Participant;

@Component("participantService")
public class ParticipantService {

	DatabaseConnector connector;

	public ParticipantService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Participant> getAll() {
		return connector.getSession().createCriteria(Participant.class).list();
	}

	public Participant findByLogin(String login) {
		return (Participant) connector.getSession().createCriteria(Participant.class, login).list().get(0);
	}
	
	public void add(Participant participant) {
		
	}
	
}
