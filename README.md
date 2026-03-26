# Sprout : 디지털 정원 노트 서비스

### 🔍 Overview

Sprout는 노트 간의 연관 관계를 기반으로 지식을 탐색할 수 있는 노트 서비스입니다. 기존 노트 서비스는 단순 저장 기능에 집중되어 있어 노트 간 관계를 탐색하거나 새로운 인사이트를 얻기 어렵다는 문제가 있었습니다. 따라서 노트 간 연결성과 추천 기능을 통해 지식 확장을 돕는 것을 목표로 개발하였습니다.

- Kotlin, Spring Boot, Kafka, SQS, PostgreSQL, Redis, AWS
- 개인 프로젝트 (2025.07 - 2025.09)

### 🛠️ Tech Stack

- Backend: Kotlin, Spring Boot (JPA, QueryDSL)
- Frontend: React, Vite, Tailwind CSS, Axios
- Database: PostgreSQL, Redis
- Messaging: Kafka, SQS
- Infra: AWS (Elastic Beanstalk, RDS, ElastiCache), Vercel, GitHub Actions

### 📐 Architecture

- System Architecture
![Sprout system architecture.png](readme_assets/Sprout%20system%20architecture.png)

- Deployment Architecture
![Sprout deployment architecture.png](readme_assets/Sprout%20deployment%20architecture.png)

### 👩‍💻 Key Implementations

- OpenAI Embedding + 메시지 큐 기반 비동기 추천 시스템 구현
- Spring Profile 기반 Kafka/SQS 선택 구조 설계
- pg_trgm + GIN 인덱스 기반 유사도 검색 및 성능 최적화
- QueryDSL 기반 No-offset 페이징으로 대량 데이터 조회 개선
- SSE 기반 실시간 알림 처리
- Flyway 기반 데이터베이스 마이그레이션 자동화
- GitHub Actions, AWS Elastic Beanstalk, Vercel 기반 배포 및 운영 자동화 구축

### 🧠 Problem Solving

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
