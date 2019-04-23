package com.company.enroller.controllers;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.company.enroller.model.Participant;
import com.company.enroller.model.Meeting;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;

@RunWith(SpringRunner.class)
@WebMvcTest(MeetingRestController.class)
public class MeetingRestControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private MeetingService meetingService;

	@MockBean
	private ParticipantService participantService;

	private Meeting meeting;
	private Meeting updatedMeeting;
	private Participant participant;

	private String meetingInputJSON;
	private String updatedMeetingJSON;
	private String participantJSON;

	@Before
	public void setup() {
		meeting = new Meeting();
		meeting.setId(1);
		meeting.setTitle("meetingtitle");
		meeting.setDescription("meetingdescription");
		meeting.setDate("22-04-2019");
		meetingInputJSON = "{\"id\": \"1\", \"title\": \"meetingtitle\", \"description\": \"meetingdescription\", \"date\": \"22-04-2019\"}";

		updatedMeeting = new Meeting();
		updatedMeeting.setId(meeting.getId());
		updatedMeeting.setTitle("updated meetingtitle");
		updatedMeeting.setDescription("updated meeting description");
		updatedMeeting.setDate("23-05-2019");
		updatedMeetingJSON = "{\"id\": \"1\", \"title\": \"updated meetingtitle\", \"description\": \"updated meeting description\", \"date\": \"23-05-2019\"}";

		participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");
		participantJSON = "{\"login\": \"testlogin\", \"password\": \"testpassword\"}";
	}

	@Test
	public void testGetMeetings() throws Exception {
		Collection<Meeting> allMeetings = singletonList(meeting);
		given(meetingService.getAll()).willReturn(allMeetings);
		mvc.perform(get("/meetings").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].title", is(meeting.getTitle())));
	}

	@Test
	public void testGetMeeting() throws Exception {
		given(meetingService.get(meeting.getId())).willReturn(meeting);
		mvc.perform(get("/meetings/" + meeting.getId()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.title", is(meeting.getTitle())));

		given(meetingService.get(meeting.getId())).willReturn((Meeting) null);
		mvc.perform(get("/meetings/" + meeting.getId()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testGetMeetingParticipants() throws Exception {
		Collection<Participant> meetingParticipants = singletonList(participant);
		given(meetingService.getParticipants(meeting.getId())).willReturn(meetingParticipants);
		mvc.perform(get("/meetings/" + meeting.getId() + "/participants").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].login", is(participant.getLogin())));

		given(meetingService.getParticipants(meeting.getId())).willReturn((Collection<Participant>) null);
		mvc.perform(get("/meetings/" + meeting.getId() + "/participants").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testAddMeeting() throws Exception {
		given(meetingService.add(meeting)).willReturn(meeting);
		mvc.perform(post("/meetings").content(meetingInputJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.title", is(meeting.getTitle())));

		given(meetingService.get(meeting.getId())).willReturn(meeting);
		mvc.perform(post("/meetings").content(meetingInputJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());
	}

	@Test
	public void testAddParticipant() throws Exception {
		long meetingId = meeting.getId();

		given(meetingService.get(meetingId)).willReturn(meeting);
		given(meetingService.addParticipant(meetingId, participant)).willReturn(participant);
		mvc.perform(post("/meetings/" + meeting.getId() + "/participants").content(participantJSON)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.login", is(participant.getLogin())));

		given(meetingService.get(meetingId)).willReturn((Meeting) null);
		mvc.perform(post("/meetings/" + meeting.getId() + "/participants").content(participantJSON)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

	@Test
	public void testUpdateMeeting() throws Exception {
		given(meetingService.get(meeting.getId())).willReturn(meeting);
		mvc.perform(
				put("/meetings/" + meeting.getId()).content(updatedMeetingJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.title", is(updatedMeeting.getTitle())));

		given(meetingService.get(meeting.getId())).willReturn(null);
		mvc.perform(
				put("/meetings/" + meeting.getId()).content(updatedMeetingJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
}
