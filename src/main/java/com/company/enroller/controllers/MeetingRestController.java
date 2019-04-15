package com.company.enroller.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

	@Autowired
	MeetingService meetingService; 
	
	@Autowired
	ParticipantService participantService;

	// Get meetings
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getAllMettings() {
		Collection<Meeting> meetings = meetingService.getAll();
		return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
	}
	
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getMeeting(@PathVariable("id") long id) {
		Meeting meeting = meetingService.getMeeting(id);
		if (meeting == null)
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
	public ResponseEntity<?> getAllMeetingParticipants(@PathVariable("id") long meetingId) {
		Collection<Participant> participants = meetingService.getParticipants(meetingId);
		if (participants == null) 
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
	}
	
	// Add meeting
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> addParticipant(@RequestBody Meeting newMeeting) {
		if (meetingService.getMeeting(newMeeting.getId()) != null) 
			return new ResponseEntity("Unable to add, meeting already exist", HttpStatus.CONFLICT);
		meetingService.add(newMeeting);
		return new ResponseEntity("Meeting added", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}/participants", method = RequestMethod.POST)
	public ResponseEntity<?> addParticipantToMeeting(@PathVariable("id") long meetingId, @PathVariable("participantId") String participantId) {
		return new ResponseEntity("Participant added", HttpStatus.OK);
	}
	
}