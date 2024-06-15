// Function to create stars
function createStars(numberOfStars) {
    const loginBox = document.querySelector('.login');  // 获取登录框的位置和大小
    const boxRect = loginBox.getBoundingClientRect();

    for (let i = 0; i < numberOfStars; i++) {
        let star = document.createElement('div');
        star.className = 'star';

        let x, y;
        do {
            x = Math.random() * window.innerWidth;
            y = Math.random() * window.innerHeight;
            // 重新计算星星位置直到它不在登录框内
        } while (x >= boxRect.left && x <= boxRect.right && y >= boxRect.top && y <= boxRect.bottom);

        star.style.left = `${x}px`;
        star.style.top = `${y}px`;
        star.style.animationDuration = `${Math.random() * 3 + 1}s`; // Randomize animation speed

        // Randomize star color between light yellow and white
        const yellowRatio = Math.random(); // 0 (more white) to 1 (more yellow)
        const red = 255;
        const green = 255;
        const blue = 255 * (1 - yellowRatio * 0.2); // Reduce blue to add yellow tint
        star.style.backgroundColor = `rgb(${red}, ${green}, ${blue})`;

        document.body.appendChild(star);
    }
}

// Create 222 stars
createStars(222);

