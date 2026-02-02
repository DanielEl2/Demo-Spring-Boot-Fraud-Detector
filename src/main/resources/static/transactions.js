
let LoggedIn = false;
document.addEventListener('DOMContentLoaded', () => {
    // Only run getTransactions if the transaction table exists
    const role = localStorage.getItem("role");
    const userId = localStorage.getItem("userId");
    const token = localStorage.getItem("token"); // Assuming you store the JWT here
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
    const mainContent = document.getElementById('main-content');
    const authError = document.getElementById('auth-error');

    // 1. Check if the user is logged in
    if (!token || !userId) {
        if (mainContent) mainContent.style.display = 'none';
        if (authError) authError.style.display = 'block';
        return; // Stop execution here
    }
    if (document.getElementById('table-body')) {
        getTransactions(userId,role); // Run immediately once
        setInterval(()=>getTransactions(userId,role), 2000); // Then repeat
    }

    // Only run getAlerts if the alert table exists
    if (document.getElementById('alert-table')) {
        getAlerts(userId,role); // Run immediately once
        setInterval(()=>getAlerts(userId,role), 2000); // Then repeat
    }

    if(document.getElementById('main-dashboard')){
        getHomeInfo();
        setInterval(getHomeInfo,2000);
    }

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

async function getTransactions(userId,role) {
    try {
        let response;
        if(role==='USER'){
            response = await fetch(`http://localhost:8080/transactions/${userId}`,{
                method:'GET',
                headers: {'Content-Type': 'application/json','Authorization':`Bearer ${localStorage.getItem('token')}`}
            });
        }
        else{
            response = await fetch('http://localhost:8080/transactions',{
                method:'GET',
                headers: {'Content-Type': 'application/json','Authorization':`Bearer ${localStorage.getItem('token')}`}
            });
        }

        const data = await response.json();

        data.reverse();

        const tableBody = document.getElementById('table-body');
        // Safety check: if user switched pages quickly
        if (!tableBody) return;

        let dataHtml = '';
        let fraudCounter = 0;

        for (let transaction of data) {
            console.log(transaction.status);
            let statusClass = 'status-clean';
            if (transaction.status === 'fraud') {
                fraudCounter++;
                statusClass = 'status-fraud';
            }

            dataHtml += `
                <tr>
                    <td class="id-col">${transaction.transaction_id}</td>
                    <td>${transaction.location}</td>
                    <td>$${transaction.amount.toFixed(2)}</td>
                    <td>${transaction.currency}</td>
                    <td>${transaction.timestamp}</td>
                    <td>${transaction.user}</td>
                    <td>
                        <span class="status-badge ${statusClass}">
                            ${transaction.status.toUpperCase()}
                        </span>
                    </td>
                </tr>`;
        }

        // Update table
        tableBody.innerHTML = dataHtml;

        // Update badge (Only if badge exists on this page)
        const alertsBadge = document.getElementById('alerts');
        if (alertsBadge) alertsBadge.textContent = `${fraudCounter}`;

    } catch (error) {
        console.error("Error fetching transactions:", error);
    }
}

async function getAlerts(userId,role) {
    try {
        let response;
        if(role==='USER'){
            response = await fetch(`http://localhost:8080/alerts/${userId}`,{
                method:'GET',
                headers:{'Content-Type':'application/json','Authorization':`Bearer ${localStorage.getItem('token')}`}
            })

        }
        else{
             response = await fetch('http://localhost:8080/alerts',{
                method:'GET',
                headers: {'Content-Type': 'application/json','Authorization':`Bearer ${localStorage.getItem('token')}`}
            });
        }
        if(!response.ok){
            console.error(`Error: Server returned ${response.status}`);
            return;
         }

        const data = await response.json();

        const tableBody = document.getElementById('alert-table');
        if (!tableBody) return;

        let dataHtml = '';
        let alertCounter = 0;

        data.reverse();


        for (let alertItem of data) {
            if (alertItem.status==='FRAUD' || alertItem.status==='OPEN') {
                alertCounter++;
            }



            dataHtml += `
                 <tr>
                     <td class="id-col">${alertItem.id}</td> 
                     <td>${alertItem.transaction_id}</td>
                     <td>${alertItem.description}</td>
                     <td>${alertItem.timeStamp}</td>
                     <td>${alertItem.status}</td>
                     
                     
                     <td>
                        <button onclick="resolve('${alertItem.id}')" class="resolve-btn">Resolve</button>
                        <button onclick="remove('${alertItem.id}')" class="reject-btn">Reject</button>
                     </td>
                 </tr>`;
        }

        tableBody.innerHTML = dataHtml;

        const alertsBadge = document.getElementById('alertt');
        if (alertsBadge) alertsBadge.textContent = `${alertCounter}`;

    } catch (error) {
        console.error("Error fetching alerts:", error);
    }
}

async function resolve(id){
    try{
        const response = await fetch(`http://localhost:8080/alerts/${id}`,
            {method: 'PATCH',
                headers: {'Content-Type': 'application/json',Authorization: `Bearer ${localStorage.getItem('token')}`},
                body: JSON.stringify({status: 'ACCEPTED'})


            });
    }
    catch(error){
        console.error("Error resolving alert:", error);
    }

}

async function remove(id){
    try {
        const response = await fetch(`http://localhost:8080/alerts/${id}`,
            {
                method: 'PATCH',
                headers: {'Content-Type': 'application/json','Authorization':`Bearer ${localStorage.getItem('token')}`},
                body: JSON.stringify({status: 'FRAUD'})


            });
    }
    catch(error){
        console.error("Error removing alert:", error);
    }
}

async function getHomeInfo() {
    try {
        const role = localStorage.getItem("role");
        const userId = localStorage.getItem("userId");
        let response;
        if(role==='USER'){
            response = await fetch(`http://localhost:8080/transactions/${userId}`,{
                method:'GET',
                headers: {'Content-Type': 'application/json','Authorization':`Bearer ${localStorage.getItem('token')}`}
            });
        }
        else{
            response = await fetch('http://localhost:8080/transactions',{
                method:'GET',
                headers: {'Content-Type': 'application/json','Authorization':`Bearer ${localStorage.getItem('token')}`}
            });
        }

        const data = await response.json();
        let total = 0;
        let alerts = 0;
        for (let transaction of data) {
            if (transaction.status === 'fraud') {
                alerts++;
            }
            total += transaction.amount;

        }

        document.getElementById('total').textContent = `$${total.toFixed(2)}`;
        document.getElementById('threats').textContent = `${alerts}`;
        document.getElementById('home-alerts').textContent = `${alerts}`;
    }
    catch(error){
        console.error("Error fetching home info:", error);
    }

}

async function logout(){
    localStorage.clear();
    window.location.href = "login.html";
}


async function updateUser() {
    let firstName = document.getElementById('First').value;
    let lastName = document.getElementById('Last').value;
    let username = document.getElementById('Username').value;
    let email = document.getElementById('email').value;
    let password = document.getElementById('password').value;

    const userdata={
    }
    if(firstName!==""){
        userdata.firstName = firstName;
    }
    if(lastName!==""){
        userdata.lastName = lastName;
    }
    if(username!==""){
        userdata.username = username;
    }
    if(email!==""){
        userdata.email = email;
    }
    if(password!==""){
        userdata.password = password;
    }

    if(Object.keys(userdata).length===0){
        return alert("Please fill in at least one field");
    }
    try {
        const response = await fetch(`http://localhost:8080/users/${localStorage.getItem('userId')}`, {
            method: 'PATCH',
            headers: {'content-type': 'application/json', 'Authorization': `Bearer ${localStorage.getItem('token')}`},
            body: JSON.stringify(userdata)
        })


        if (response.ok) {
            if (userdata.username !=="") {
                localStorage.setItem('username', userdata.username);
                const buttoncont = document.getElementById('buttonContainer');

                buttoncont.innerHTML = `
                    <ion-icon name="person-outline"></ion-icon>
                    <p id="NavUserName">${userdata.username}</p>
                `;


            }
            console.log("good")
        } else {
            const errormsg = document.getElementById('updateError');
            errormsg.innerHTML = `${response.message}`;
        }
    }
    catch(error){
        console.error("Error updating user:", error);
    }
}

