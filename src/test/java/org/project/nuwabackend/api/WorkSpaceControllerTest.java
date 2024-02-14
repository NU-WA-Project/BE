package org.project.nuwabackend.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceRequestDto;
import org.project.nuwabackend.dto.workspace.response.WorkSpaceIdResponse;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.WorkSpaceService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.project.nuwabackend.global.type.GlobalResponseStatus.SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.CREATE_WORK_SPACE_SUCCESS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("[API] WorkSpace Controller Test")
@ExtendWith(MockitoExtension.class)
class WorkSpaceControllerTest {

    @Mock
    WorkSpaceService workSpaceService;

    @Mock
    GlobalService globalService;

    @InjectMocks
    WorkSpaceController workSpaceController;

    private MockMvc mvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    String workSpaceName = "workSpaceName";
    String workSpaceImage = "workSpaceImage";
    String workSpaceIntroduce = "workSpaceIntroduce";
    String workSpaceMemberName = "workSpaceMemberName";
    String workSpaceMemberJob = "workSpaceMemberJob";
    String workSpaceMemberImage = "workSpaceMemberImage";

    private WorkSpaceRequestDto workSpaceRequestDto() {
        return new WorkSpaceRequestDto(
                workSpaceName, workSpaceImage,
                workSpaceIntroduce, workSpaceMemberName,
                workSpaceMemberJob, workSpaceMemberImage);
    }

    @BeforeEach
    void setup() {
       mvc = MockMvcBuilders.standaloneSetup(workSpaceController).build();
    }

    @Test
    @DisplayName("[API] Create WorkSpace Test")
    void createWorkSpaceTest() throws Exception {
        //given
        String body = objectMapper.writeValueAsString(workSpaceRequestDto());
        Long workSpaceId = 1L;
        WorkSpaceIdResponse workSpaceIdResponse = new WorkSpaceIdResponse(workSpaceId);

        GlobalSuccessResponseDto<Object> createWorkSpaceSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(CREATE_WORK_SPACE_SUCCESS.getMessage())
                        .data(workSpaceIdResponse)
                        .build();

        given(workSpaceService.createWorkSpace(any(), any()))
                .willReturn(workSpaceId);
        given(globalService.successResponse(anyString(), any()))
                .willReturn(createWorkSpaceSuccessResponse);

        //when
        //then
        mvc.perform(post("/api/workspace")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status")
                        .value(SUCCESS.getValue()))
                .andExpect(jsonPath("$.message")
                        .value(CREATE_WORK_SPACE_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.workSpaceId")
                        .value(workSpaceId))
                .andDo(print());
    }
}