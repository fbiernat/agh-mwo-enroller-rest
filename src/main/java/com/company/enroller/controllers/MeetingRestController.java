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

	private MeetingService meetingService;
	private ParticipantService participantService;
	
	@Autowired
	public void setMeetingService(MeetingService meetingService) {
		this.meetingService = meetingService;
	}

	@Autowired
	public void setParticipantService(ParticipantService participantService) {
		this.participantService = participantService;
	}

	// Get all meetings
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getAll() {
		Collection<Meeting> meetings = meetingService.getAll();

		return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
	}

	// Get specific meeting
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> get(@PathVariable("id") long id) {
		Meeting meeting = meetingService.get(id);
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
		if (meetingService.get(newMeeting.getId()) != null)
			return new ResponseEntity("Unable to add, meeting already exist", HttpStatus.CONFLICT);
		meetingService.add(newMeeting);

		return new ResponseEntity(newMeeting, HttpStatus.OK);
	}

	// Add participant to the meeting
	@RequestMapping(value = "/{id}/participants", method = RequestMethod.POST)
	public ResponseEntity<?> addParticipantToMeeting(@PathVariable("id") long meetingId,
			@RequestBody Participant newParticipant) {
		Meeting meeting = meetingService.get(meetingId);
		if (meeting == null)
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		meetingService.addParticipant(meetingId, newParticipant);

		return new ResponseEntity(newParticipant, HttpStatus.OK);
	}

	// 2 ---------------------------------------------------------------
	// Update meeting
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateMeeting(@PathVariable("id") long meetingId, @RequestBody Meeting updatedMeeting) {
		Meeting meeting = meetingService.get(meetingId);
		if (meeting == null)
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		meeting.setTitle(updatedMeeting.getTitle());
		meeting.setDescription(updatedMeeting.getDescription());
		meeting.setDate(updatedMeeting.getDate());
		meetingService.update(meeting);

		return new ResponseEntity(meeting, HttpStatus.OK);
	}

	// Remove meeting's participant
	@RequestMapping(value = "/{id}/participants/{participantId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteParticipant(@PathVariable("id") long meetingId,
			@PathVariable("participantId") String login) {
		Meeting meeting = meetingService.get(meetingId);
		Participant participant = participantService.findByLogin(login);
		if (meeting == null || participant == null)
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		meeting.removeParticipant(participant);
		meetingService.update(meeting);

		return new ResponseEntity(meeting, HttpStatus.OK);
	}

	// Delete meeting
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMeeting(@PathVariable("id") long meetingId) {
		Meeting meeting = meetingService.get(meetingId);
		if (meeting == null)
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		meetingService.delete(meeting);
		
		return new ResponseEntity(HttpStatus.OK);
	}

	// 3 ---------------------------------------------------------------
	// Sort meetings by title
	@RequestMapping(value = "/sort/{attr}", method = RequestMethod.GET)
	public ResponseEntity<?> getSortedMeetings(@PathVariable("attr") String attr) {
		Collection<Meeting> result = null;
		if (attr.toLowerCase().equals("title")) {
			result = meetingService.getSortedByTitle();
		} else {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		if (result == null)
			return new ResponseEntity(HttpStatus.NOT_FOUND);

		return new ResponseEntity<Collection<Meeting>>(result, HttpStatus.OK);
	}

	// Search in meetings' titles and descriptions
	@RequestMapping(value = "/search/titledesc/{phrase}", method = RequestMethod.GET)
	public ResponseEntity<?> searchMeeting(@PathVariable("phrase") String phrase) {
		Collection<Meeting> result = null;
		result = meetingService.searchTitleAndDesc(phrase);
		if (result == null || result.size() == 0)
			return new ResponseEntity(HttpStatus.NOT_FOUND);

		return new ResponseEntity<Collection<Meeting>>(result, HttpStatus.OK);
	}

	// Search meeting by participant
	@RequestMapping(value = "/search/participants/{login}", method = RequestMethod.GET)
	public ResponseEntity<?> searchMeetingByParticipant(@PathVariable("login") String participant) {
		System.out.println(participant);
		if (participant.equals(""))
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		Collection<Meeting> result = null;
		result = meetingService.searchByParticipant(participant);
		if (result.size() == 0)
			return new ResponseEntity(HttpStatus.NOT_FOUND);

		return new ResponseEntity<Collection<Meeting>>(result, HttpStatus.OK);
	}

}