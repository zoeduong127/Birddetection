const parent = "signup";
let parentSelection = document.querySelector(".signup");
const child = "signup2";
let childSelection = document.querySelector(".signup2");
let email;
let name;
let password;
let repeat;
let tel;


const inputElements = document.querySelectorAll(`.${parent} input`);
const inputChildElements = document.querySelectorAll(`.${child} input`);

inputElements.forEach(input => {
    input.addEventListener("keypress", function (event) {
        if (event.key === "Enter") {
            console.log("signup event clicked");

            email = document.getElementById("Email").value;
            name = document.getElementById("Name").value;
            if (email !== "" && this.name !== "") {
                parentSelection.style.display = "none";
                childSelection.style.display = "flex";
            }
        }
    })
});

function submit() {
    console.log("button clicked");

    email = document.getElementById("Email").value;
    name = document.getElementById("Name").value;
    if (email !== "" && name !== "") {
        parentSelection.style.display = "none";
        childSelection.style.display = "flex";
    }
}

//TODO: this isn't really secure since we can see and compare the passwords if we give it to the console log. is this fine or should we change it. 
inputChildElements.forEach(input => {
    input.addEventListener("keypress", function (event) {
        if (event.key === "Enter") {
            console.log("signup2 event triggered");

            password = document.getElementById("password").value;
            repeat = document.getElementById("repeat-password").value;
            // console.log(password);
            // console.log(repeat);
            tel = document.getElementById("phone").value;

            if (password === repeat) {
                if (password !== "" && repeat !== "") {
                    console.log("If statement entered");
                    signup(event);
                }
            } else {
                alert("Please ensure that the passwords match")
            }
        }
    })
})

function startSignup() {
    password = document.getElementById("password").value;
    repeat = document.getElementById("repeat-password").value;
    // console.log(password);
    // console.log(repeat);
    tel = document.getElementById("phone").value;

    if (password === repeat) {
        if (password !== "" && repeat !== "") {
            console.log("If statement entered");
            signup(event);
        }
    } else {
        alert("Please ensure that the passwords match")
    }
}

function signup(event) {
    event.preventDefault();
    // Prevents the default form submission behavior


    const url = window.location.origin + "/bad/api/accounts";
    const account = {
        id: null,
        username: name,
        email: email,
        passwordHash: password,
        telephone: tel,
        salt: null,
    };
    console.log(account);
    const options = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        },
        body: JSON.stringify(account)
    };
    fetch(url, options)
        .then(response => {
            if (response.ok) {
                window.location.href = "./H-login.html";
                console.log("Should be forwarded to the main page.")
            } else {
                name = "";
                email = "";
                password = "";
                tel = "";

                parentSelection.style.display = "flex";
                childSelection.style.display = "none";
                response.text().then(err => alert("An error occurred: \n" + err));
            }
        })
        .catch(error => {
            console.error("Signup error:", error);
            alert("An error occurred while processing your signup.");
        });
}