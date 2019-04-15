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

	// Get all meetings
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getAll() {
		Collection<Meeting> meetings = meetingService.getAll();
		return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
	}

	// Get specific meeting
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public ResponseEntity<?> get(@PathVariable("id") long id) {
		Meeting meeting = meetingService.getMeeting(id);
		if (meeting == null)
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}

	// Get meeting's participants
	@RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
	public ResponseEntity<?> getAllParticipants(@PathVariable("id") long meetingId) {
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

	// Add participant to the meeting
	@RequestMapping(value = "/{id}/participants", method = RequestMethod.POST)
	public ResponseEntity<?> addParticipantToMeeting(@PathVariable("id") long meetingId,
			@RequestBody Participant newParticipant) {
		Meeting meeting = meetingService.getMeeting(meetingId);
		if (meeting == null)
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		meetingService.addParticipant(meetingId, newParticipant);

		return new ResponseEntity("Participant added", HttpStatus.OK);
	}

	// Update meeting
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateMeeting(@PathVariable("id") long meetingId, @RequestBody Meeting updatedMeeting) {
		Meeting meeting = meetingService.getMeeting(meetingId);
		if (meeting == null)
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		meeting.setTitle(updatedMeeting.getTitle());
		meeting.setDescription(updatedMeeting.getDescription());
		meeting.setDate(updatedMeeting.getDate());
		meetingService.update(meeting);

		return new ResponseEntity(this.getAll().getBody(), HttpStatus.OK);
	}

	// Remove meeting's participant
	@RequestMapping(value = "/{id}/participants/{participantId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteParticipant(@PathVariable("id") long meetingId,
			@PathVariable("participantId") String login) {
		Meeting meeting = meetingService.getMeeting(meetingId);
		Participant participant = participantService.findByLogin(login);
		if (meeting == null || participant == null)
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		meeting.removeParticipant(participant);
		meetingService.update(meeting);

		return new ResponseEntity(this.getAll().getBody(), HttpStatus.OK);
	}

}