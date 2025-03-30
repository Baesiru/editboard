document.addEventListener("DOMContentLoaded", function () {
    const apiUrl = "http://127.0.0.1:8080/api/boards";
    let currentPage = 0;
    const pageSize = 10; // 기본 사이즈

    function fetchBoardData(page = 0) {
        fetch(`${apiUrl}?page=${page}&size=${pageSize}`)
            .then(response => response.json())
            .then(data => {
                console.log(data);
                updatePagination(data);  // 페이지네이션 업데이트
                updateBoardData(data);   // 게시글 데이터 업데이트
            })
            .catch(error => console.error("Error fetching board data:", error));
    }

    // 게시글 데이터를 테이블에 표시
    function updateBoardData(data) {
        const tbody = document.querySelector("table tbody");
        tbody.innerHTML = ""; // 기존 데이터 초기화

        data.content.forEach(board => {
            const row = document.createElement("tr");

            // 데이터 삽입
            row.innerHTML = `
                <td>${board.id}</td>
                <td><a href="BoardDetail.html?id=${board.id}">${board.title}</a></td>
                <td>${board.username}</td>
                <td>${new Date(board.createdAt).toLocaleDateString()}</td>
                <td>${board.viewCount}</td>
            `;
            tbody.appendChild(row);
        });
    }

    // 페이지네이션 업데이트
    function updatePagination(data) {
        const pagination = document.getElementById("pagination");
        pagination.innerHTML = ""; // 기존 페이지네이션 초기화

        // 이전 버튼
        const prevButton = document.createElement("li");
        prevButton.className = `page-item ${data.first ? "disabled" : ""}`;
        prevButton.innerHTML = `<a class="page-link" href="#">이전</a>`;
        prevButton.addEventListener("click", () => {
            if (!data.first) fetchBoardData(currentPage - 1);
        });
        pagination.appendChild(prevButton);

        // 페이지 번호 버튼
        for (let i = 0; i < data.totalPages; i++) {
            const pageButton = document.createElement("li");
            pageButton.className = `page-item ${i === data.number ? "active" : ""}`;
            pageButton.innerHTML = `<a class="page-link" href="#">${i + 1}</a>`;
            pageButton.addEventListener("click", () => fetchBoardData(i));
            pagination.appendChild(pageButton);
        }

        // 다음 버튼
        const nextButton = document.createElement("li");
        nextButton.className = `page-item ${data.last ? "disabled" : ""}`;
        nextButton.innerHTML = `<a class="page-link" href="#">다음</a>`;
        nextButton.addEventListener("click", () => {
            if (!data.last) fetchBoardData(currentPage + 1);
        });
        pagination.appendChild(nextButton);

        // 현재 페이지 업데이트
        currentPage = data.number;
    }
    
    const createPostBtn = document.getElementById("createPostBtn");
    if (createPostBtn) {
        createPostBtn.addEventListener("click", function () {
            window.location.href = "BoardCreate.html";
        });
    }

    // 첫 로드 시 데이터 가져오기
    fetchBoardData();
});
