document.addEventListener('DOMContentLoaded', function() {
  
   const navLinks = document.querySelectorAll('.navbar-nav .nav-link');

 
   navLinks.forEach(link => {
     link.addEventListener('click', function() {

       navLinks.forEach(l => l.classList.remove('active'));
  
       this.classList.add('active');
     });
   });

   const currentPath = window.location.pathname;
   navLinks.forEach(link => {
     if (link.getAttribute('href') === currentPath) {
       navLinks.forEach(l => l.classList.remove('active'));
       link.classList.add('active');
     }
   });
 });