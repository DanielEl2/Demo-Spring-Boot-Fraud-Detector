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

async function sendLogin(){

    const userData = {
        username: document.getElementById('username').value,
        password: document.getElementById('password').value
    };
    const response = await fetch("http://localhost:8080/Login", {
            method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
        }
    )

    if(response.ok){
        const data = await response.json();
        localStorage.setItem('token',data.token);
        localStorage.setItem('username',data.username);
        localStorage.setItem('role',data.role);
        localStorage.setItem('userId',data.userId);
        window.location.href = "index.html";
    }
    else{
        const err = document.getElementById('error');
        err.innerHTML = "Invalid username or password";
    }

}