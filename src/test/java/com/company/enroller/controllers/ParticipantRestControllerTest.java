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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;

@RunWith(SpringRunner.class)
@WebMvcTest(ParticipantRestController.class)
public class ParticipantRestControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private MeetingService meetingService;

	@MockBean
	private ParticipantService participantService;

	@Test
	public void testGetParticipants() throws Exception {
		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");

		Collection<Participant> allParticipants = singletonList(participant);
		given(participantService.getAll()).willReturn(allParticipants);

		mvc.perform(get("/participants").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].login", is(participant.getLogin())));
	}

	@Test
	public void testAddParticipant() throws Exception {
		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");
		String inputJSON = "{\"login\": \"testlogin\", \"password\": \"somepassword\"}";

		given(participantService.findByLogin("testlogin")).willReturn((Participant) null);
		given(participantService.add(participant)).willReturn(participant);
		mvc.perform(post("/participants").content(inputJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.login", is(participant.getLogin())));

		given(participantService.findByLogin("testlogin")).willReturn(participant);
		mvc.perform(post("/participants").content(inputJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());

		verify(participantService, times(2)).findByLogin("testlogin");
	}

	@Test
	public void testUpdateParticipant() throws Exception {
		Participant updatedParticipant = new Participant();
		updatedParticipant.setLogin("testlogin");
		updatedParticipant.setPassword("newpassword");
		String uInputJSON = "{\"login\": \"testlogin\", \"password\": \"newpassword\"}";

		given(participantService.findByLogin("testlogin")).willReturn((Participant) null);
		given(participantService.update(updatedParticipant)).willReturn(updatedParticipant);
		mvc.perform(put("/participants/" + updatedParticipant.getLogin()).content(uInputJSON)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("oldpassword");

		given(participantService.findByLogin("testlogin")).willReturn(participant);
		given(participantService.update(updatedParticipant)).willReturn(updatedParticipant);
		mvc.perform(put("/participants/" + updatedParticipant.getLogin()).content(uInputJSON)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.login", is(updatedParticipant.getLogin())));

		verify(participantService, times(2)).findByLogin("testlogin");
	}

	@Test
	public void testDeleteParticipant() throws Exception {
		Participant participant = new Participant();
		participant.setLogin("login");
		participant.setPassword("password");

		given(participantService.findByLogin(participant.getLogin())).willReturn((Participant) null);
		given(participantService.delete(participant)).willReturn(participant);
		mvc.perform(delete("/participants/" + participant.getLogin())).andExpect(status().isNotFound());

		given(participantService.findByLogin(participant.getLogin())).willReturn(participant);
		given(participantService.delete(participant)).willReturn(participant);
		mvc.perform(delete("/participants/" + participant.getLogin())).andExpect(status().isOk())
				.andExpect(jsonPath("$.login", is(participant.getLogin())));

		verify(participantService, times(2)).findByLogin(participant.getLogin());
	}
}
