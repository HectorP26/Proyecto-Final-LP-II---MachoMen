

document.addEventListener("DOMContentLoaded", function () {
    const navbar = document.querySelector('.navbar');
    const logo = document.querySelector('.letra-logo'); 

    window.addEventListener('scroll', function () {
        if (window.scrollY > 50) {
           
            navbar.classList.remove('bg-transparent', 'navbar-dark');
            navbar.classList.add('bg-white', 'navbar-light', 'shadow-sm');
          
            logo.style.color = "black";
        } else {
            
            navbar.classList.remove('bg-white', 'navbar-light', 'shadow-sm');
            navbar.classList.add('bg-transparent', 'navbar-dark');
            
            
            logo.style.color = "white";
        }
    });
});


