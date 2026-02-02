let LoggedIn = false;
document.addEventListener('DOMContentLoaded',()=>{
    const signupForm = document.querySelector('section');
    signupForm.style.opacity='0';
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    const buttoncont = document.getElementById('buttonContainer');
    if(token){
        LoggedIn=true;
        buttoncont.innerHTML += `
        <ion-icon name="person-outline"></ion-icon>
        <p id="NavUserName">${username}</p>
    `;

    }
    else{
        buttoncont.innerHTML+='<p id="NavUserName">Log In</p>'
    }

    setTimeout(()=>{
        signupForm.style.transition='opacity 1s ease-in-out';
        signupForm.style.opacity='1';
    },500)

});

const profileButton = document.getElementById('dropbutton')
const content = document.getElementById('dcontent')
profileButton.addEventListener("click",()=>{
    if(LoggedIn){
        content.style.display='flex';
    }
    else{
        window.location.href = "login.html";
    }

});

document.addEventListener("click",(event)=>{
    if(!content.contains(event.target) && !profileButton.contains(event.target)){
        content.style.display='none';
    }
})


async function createUser(){
    const userData = {
        firstName: document.getElementById('First').value,
        lastName: document.getElementById('last').value,
        username: document.getElementById('Username').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value
    };

    const response = await fetch('http://localhost:8080/SignUp',{
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(userData)
    })
    const data = await response.json();

    if(response.ok){
        localStorage.setItem(`token`,data.token)
        localStorage.setItem(`username`,data.username)
        localStorage.setItem(`role`,data.role)
        localStorage.setItem('userId',data.userId)
        window.location.href = "index.html";
    }
    else{
        if(response.status===409){
            const err = document.getElementById('signUperror');
            err.innerHTML = data.message;
            if(response.message==='Username already exists'){
                err.innerHTML = 'Username already exists';

            }
            else if(response.message==='Email already exists'){
                err.innerHTML = 'Email already exists';
            }
        }
        else{
            alert("Something went wrong, please try again later");
        }
    }
}