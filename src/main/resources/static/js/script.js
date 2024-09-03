const header = document.querySelector("header");
window.addEventListener("scroll", function(){
    header.classList.toggle("sticky",this.window.scrollY > 0);
})
document.addEventListener('DOMContentLoaded', function() {
    const menuToggle = document.querySelector('.menu-toggle');
    const navMenu = document.querySelector('.navmenu');

    menuToggle.addEventListener('click', function() {
        navMenu.classList.toggle('active');
    });
});
