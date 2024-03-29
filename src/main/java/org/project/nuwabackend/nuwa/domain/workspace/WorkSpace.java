package org.project.nuwabackend.nuwa.domain.workspace;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.nuwabackend.nuwa.domain.base.BaseTimeJpa;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class WorkSpace extends BaseTimeJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_id")
    private Long id;

    @Column(name = "workspace_name")
    private String name;

    @Column(name = "workspace_image")
    private String image;

    @Column(name = "workspace_introduce")
    private String introduce;

    @Column(name = "workspace_member_count")
    private Integer count;

    @Builder
    private WorkSpace(String name, String image, String introduce) {
        this.name = name;
        this.image = image;
        this.introduce = introduce;
        this.count = 0;
    }

    // 워크스페이스 생성
    public static WorkSpace createWorkSpace(String name, String image, String introduce) {
        return WorkSpace.builder()
                .name(name)
                .image(image)
                .introduce(introduce)
                .build();
    }

    // 워크스페이스 수정
    public void updateWorkSpace(String name, String image) {
        this.name = name;
        this.image = image;
    }

    // 워크스페이스 멤버 카운트 증가
    public void increaseWorkSpaceMemberCount() {
        this.count++;
    }

    // 워크스페이스 멤버 카운트 감소
    public void decreaseWorkSpaceMemberCount() {
        this.count--;
    }
}
