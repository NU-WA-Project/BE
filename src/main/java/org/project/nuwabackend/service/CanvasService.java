package org.project.nuwabackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.mongo.Canvas;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.canvas.request.CanvasRequestDto;
import org.project.nuwabackend.dto.canvas.response.CanvasResponseDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.mongo.CanvasRepository;
import org.project.nuwabackend.service.notification.NotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;
import static org.project.nuwabackend.type.NotificationType.CANVAS;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CanvasService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final CanvasRepository canvasRepository;

    private final NotificationService notificationService;
    private final CanvasQueryService canvasQueryService;

    private static final String PREFIX = "/canvas";

    // 캔버스 생성
    @Transactional
    public void createCanvas(String email, Long workSpaceId, CanvasRequestDto canvasRequestDto) {
        String title = canvasRequestDto.title();
        String content = canvasRequestDto.content();

        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long findWorkSpaceMemberId = findWorkSpaceMember.getId();
        String findWorkSpaceMemberName = findWorkSpaceMember.getName();

        Canvas canvas =
                Canvas.createCanvas(title, content, workSpaceId, findWorkSpaceMemberId, findWorkSpaceMemberName, LocalDateTime.now());

        List<WorkSpaceMember> workSpaceMemberList = workSpaceMemberRepository.findListByWorkSpaceIdNot(workSpaceId, findWorkSpaceMemberId);

        String notificationContent = findWorkSpaceMemberName + "님이 캔버스를 생성했습니다.";
        workSpaceMemberList.forEach(workSpaceMember -> {
            notificationService.send(notificationContent, createCanvasUrl(), CANVAS, findWorkSpaceMember, workSpaceMember);
        });

        canvasRepository.save(canvas);
    }

    // 캔버스 조회
    public Slice<CanvasResponseDto> canvasList(Long workSpaceId, Long workSpaceMemberId, Pageable pageable) {

        List<CanvasResponseDto> canvasResponseDtoList = canvasQueryService.canvasList(workSpaceId, workSpaceMemberId)
                .stream().map(canvas -> CanvasResponseDto.builder()
                        .workSpaceId(workSpaceId)
                        .canvasId(canvas.getId())
                        .canvasTitle(canvas.getTitle())
                        .canvasContent(canvas.getContent())
                        .createMemberId(canvas.getCreateMemberId())
                        .createMemberName(canvas.getCreateMemberName())
                        .createdAt(canvas.getCreatedAt())
                        .build())
                .sorted(Comparator.comparing(CanvasResponseDto::createdAt).reversed()).toList();

        return canvasResponseDtoSlice(canvasResponseDtoList, pageable);
    }

    // 캔버스 수정
    public void updateCanvas(String email, Long workSpaceId, String canvasId, CanvasRequestDto canvasRequestDto) {
        String title = canvasRequestDto.title();
        String content = canvasRequestDto.content();

        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long findWorkSpaceMemberId = findWorkSpaceMember.getId();

        canvasQueryService.updateCanvas(canvasId, findWorkSpaceMemberId, workSpaceId, title, content);
    }

    // 캔버스 삭제
    public void deleteCanvas(String email, Long workSpaceId, String canvasId) {
        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long findWorkSpaceMemberId = findWorkSpaceMember.getId();

        canvasQueryService.deleteCanvas(canvasId, workSpaceId, findWorkSpaceMemberId);
    }

    // Slice(페이징)
    private Slice<CanvasResponseDto> canvasResponseDtoSlice(List<CanvasResponseDto> canvasResponseDtoList, Pageable pageable) {

        System.out.println(pageable.getSort().toList());
        boolean hasNext = canvasResponseDtoList.size() > pageable.getPageSize();
        List<CanvasResponseDto> canvasContent = hasNext ? canvasResponseDtoList.subList(0, pageable.getPageSize()) : canvasResponseDtoList;

        return new SliceImpl<>(canvasContent, pageable, hasNext);
    }

    private String createCanvasUrl() {
        return PREFIX;
    }
}
