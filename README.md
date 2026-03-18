# Sprout : 디지털 정원 노트 서비스

## 🔍 Overview

- Kotlin, Spring Boot, React 기반 노트 관리 및 지식 그래프 시각화 서비스 개발
- OpenAI Embedding을 통한 유사 노트 추천 및 pg_trgm 기반 검색 기능 구현
- Kafka, SQS 기반 비동기 메시지 처리 및 SSE 기반 실시간 알림 구현
- AWS, Vercel 환경에서 배포 및 GitHub Actions 기반 CI/CD 구축

## 🛠️ Tech Stack

- Backend: Kotlin, Spring Boot (JPA, QueryDSL)
- Database: PostgreSQL, Redis
- Messaging: Kafka, SQS
- Infra: AWS (Elastic Beanstalk, RDS, ElastiCache), Vercel, GitHub Actions

## 📐 Architecture

- System Architecture
![Sprout system architecture.png](readme_assets/Sprout%20system%20architecture.png)

- Deployment Architecture
![Sprout deployment architecture.png](readme_assets/Sprout%20deployment%20architecture.png)

## 👩‍💻 Key Implementations

- OpenAI Embedding + 메시지 큐 기반 비동기 추천 시스템 구현
- Spring Profile 기반 Kafka/SQS 선택 구조 설계
- pg_trgm + GIN 인덱스 기반 유사도 검색 및 성능 최적화
- QueryDSL 기반 No-offset 페이징으로 대량 데이터 조회 개선
- SSE 기반 실시간 알림 처리

## 🧠 Problem Solving

1. **pg_trgm 기반 검색 성능 개선**
    - LIKE 기반 검색 방식의 성능 한계
    - pg_trgm + GIN 인덱스 적용
    - 검색 성능 및 정확도 개선
2. **메시지 큐 유연성 확보**
    - 로컬 환경은 Kafka 기반으로 구성하였으나, 운영 비용 문제 발생
    - 인터페이스 추상화 및 Spring Profile 적용
    - SQS로 전환 가능한 구조 설계
3. **SSE 기반 실시간 알림 적용**
    - 비동기 처리 완료 후 실시간 상태 알림 필요
    - 비효율적인 Polling 방식 대신 SSE 도입
    - 서버 부하 감소 및 UX 개선
4. **Flyway 기반 DB 마이그레이션 자동화**
    - 환경별 스키마 불일치 문제 발생
    - 모든 DB 변경 사항을 SQL 파일로 관리할 수 있도록 Flyway 도입
    - 환경 간 일관성 및 운영 환경 안정성 확보
