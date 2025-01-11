package com.mockcote.user.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import com.mockcote.user.dto.HandleAuthRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class HandleAuthUtil {

	/**
	 * 특정 사용자의 제출 결과를 검증하는 메서드
	 *
	 * @param req 요청 객체 (사용자 핸들, 문제 번호 포함)
	 * @return 제출 결과가 조건에 맞는지 여부
	 */
	public boolean validateSubmission(HandleAuthRequest req) {
		String url = "https://www.acmicpc.net/status?user_id=" + req.getHandle();

		try {
			// URL에서 HTML 문서 가져오기
			Document document = Jsoup.connect(url).get();

			// #status-table > tbody 안의 첫 번째 tr 요소 선택
			Element tableBody = document.selectFirst("#status-table > tbody");
			if (tableBody == null) {
				throw new IllegalStateException("채점 현황 테이블을 찾을 수 없습니다.");
			}

			// 첫 번째 tr 요소 선택
			Element firstRow = tableBody.selectFirst("tr");
			if (firstRow != null) {
				// 3번째 td 요소
				Element problemElement = firstRow.select("td").get(2).selectFirst("a");
				String problemText = (problemElement != null) ? problemElement.text() : null;

				// 9번째 td 요소
				Element solveTimeElement = firstRow.select("td").get(8).selectFirst("a");
				System.out.println(solveTimeElement);
				String solveTimeText = (solveTimeElement != null) ? solveTimeElement.attr("title") : null;

				if (problemText == null || solveTimeText == null) {
					return false;
				}

				// 문제 번호 비교
				if (!req.getProblem().equals(problemText)) {
					return false;
				}

				// 시간 비교
				LocalDateTime solveTime = parseSolveTime(solveTimeText);
				LocalDateTime now = LocalDateTime.now();

				// 3분 이내인지 확인
				return ChronoUnit.MINUTES.between(solveTime, now) <= 3;
			} else {
				throw new IllegalStateException("첫 번째 행을 찾을 수 없습니다.");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("오류 발생: " + e.getMessage());
		}
	}

	/**
	 * 푼 시각 텍스트를 LocalDateTime으로 변환하는 메서드
	 *
	 * @param solveTimeText 푼 시각 텍스트
	 * @return LocalDateTime 객체
	 */
	private LocalDateTime parseSolveTime(String solveTimeText) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return LocalDateTime.parse(solveTimeText, formatter);
	}

}
