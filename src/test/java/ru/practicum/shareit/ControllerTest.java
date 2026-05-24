package ru.practicum.shareit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class ControllerTest {
    protected MockMvc mockMvc;

    protected final ObjectMapper mapper = new ObjectMapper();

    protected ResultActions performPost(String url, Object body) {
        try {
            return mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(body)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected ResultActions performPatch(String url, Object body) {
        try {
            return mockMvc.perform(MockMvcRequestBuilders.patch(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(body)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected ResultActions performGet(String url) {
        try {
            return mockMvc.perform(MockMvcRequestBuilders.get(url));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected ResultActions performDelete(String url) {
        try {
            return mockMvc.perform(MockMvcRequestBuilders.delete(url));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void expectStatusOk(ResultActions actions) {
        expect(actions, MockMvcResultMatchers.status().isOk());
    }

    private void expect(ResultActions actions, ResultMatcher matcher) {
        try {
            actions.andExpect(matcher);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void expectStatusCreated(ResultActions actions) {
        expect(actions, MockMvcResultMatchers.status().isCreated());
    }

    protected void expectStatusBadRequest(ResultActions actions) {
        expect(actions, MockMvcResultMatchers.status().isBadRequest());
    }

    protected void expectStatusConflict(ResultActions actions) {
        expect(actions, MockMvcResultMatchers.status().isConflict());
    }

    protected void expectStatusNotFound(ResultActions actions) {
        expect(actions, MockMvcResultMatchers.status().isNotFound());
    }

    protected void expectStatusNoContent(ResultActions actions) {
        expect(actions, MockMvcResultMatchers.status().isNoContent());
    }

    protected void expectStatusForbidden(ResultActions actions) {
        expect(actions, MockMvcResultMatchers.status().isForbidden());
    }

    protected void expectJSONBody(ResultActions actions, Object expectedBody) {
        try {
            expect(actions, MockMvcResultMatchers.content().json(mapper.writeValueAsString(expectedBody)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
