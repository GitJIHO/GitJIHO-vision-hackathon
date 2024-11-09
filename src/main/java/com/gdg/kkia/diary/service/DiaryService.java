package com.gdg.kkia.diary.service;

import com.gdg.kkia.common.exception.NotFoundException;
import com.gdg.kkia.common.exception.UnauthorizedException;
import com.gdg.kkia.diary.dto.DiaryReadResponse;
import com.gdg.kkia.diary.dto.DiaryWriteRequest;
import com.gdg.kkia.diary.entity.Diary;
import com.gdg.kkia.diary.repository.DiaryRepository;
import com.gdg.kkia.member.entity.Member;
import com.gdg.kkia.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void writeDiary(Long memberId, DiaryWriteRequest diaryWriteRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 멤버가 없습니다."));

        Diary diary = new Diary(diaryWriteRequest.content(), member);
        diaryRepository.save(diary);
    }

    @Transactional(readOnly = true)
    public List<DiaryReadResponse> getAllDiaryWrittenByMember(Long memberId) {
        return diaryRepository.findAllByMemberId(memberId)
                .stream()
                .map(Diary -> new DiaryReadResponse(
                        Diary.getId(),
                        Diary.getWrittenDatetime(),
                        Diary.getContent()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DiaryReadResponse getOneDiaryWrittenByMember(Long memberId, Long diaryId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 멤버가 없습니다."));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 일기가 없습니다."));

        if (diary.checkMemberIsNotCorrect(member)) {
            throw new UnauthorizedException("로그인한 사용자가 작성한 일기가 아닙니다.");
        }

        return new DiaryReadResponse(diary.getId(), diary.getWrittenDatetime(), diary.getContent());
    }

    @Transactional
    public void updateDiaryWrittenByMember(Long memberId, Long diaryId, DiaryWriteRequest diaryWriteRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 멤버가 없습니다."));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 일기가 없습니다."));

        if (diary.checkMemberIsNotCorrect(member)) {
            throw new UnauthorizedException("로그인한 사용자가 작성한 일기가 아닙니다.");
        }

        diary.updateDiary(diaryWriteRequest.content());
    }

    @Transactional
    public void deleteDiaryWrittenByMember(Long memberId, Long diaryId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 멤버가 없습니다."));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 일기가 없습니다."));

        if (diary.checkMemberIsNotCorrect(member)) {
            throw new UnauthorizedException("로그인한 사용자가 작성한 일기가 아닙니다.");
        }

        diaryRepository.deleteById(diaryId);
    }
}