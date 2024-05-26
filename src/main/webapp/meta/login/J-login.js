const input = document.querySelectorAll("body");

input.forEach(inp => {
    inp.addEventListener("keypress", function (event) {
        if (event.key === "Enter") {
            console.log("Event started");
            login(event);
        }
    })
});

function submit() {
    console.log("button clicked");
    login(event);
}


function login(event) {
    event.preventDefault();
    // Prevents the default form submission behavior

    const name = document.getElementById("logname").value;
    const password = document.getElementById("logpass").value;
    const url = window.location.origin + "/bad/api/accounts/login";
    const account = {
        email: name,
        password: password,
    };
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
                response.text().then(token => {

                    document.cookie = "auth_token=" + token + "; path=/";
                    document.cookie = "auth_token=" + token + "; path=/";
                    window.location.href = "./../main/H-main.html";

                })

                // Perform any further actions or redirect to another page
            } else {
                response.text().then(res => {
                    alert("Login unsuccessful. \n" + res);
                })
                // Clear the form inputs
                document.getElementById("email").value = "";
                document.getElementById("password").value = "";
            }
        })
        .catch(error => {
            console.error("Login error:", error);
        });
}