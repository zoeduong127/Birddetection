//TODO: [OPTIONAL] date filter with search is not combined.
//TODO: Need to add the caution when user misses 1 data for date filter
//TODO: forgot password function.

const months = [
    "Jan",
    "Feb",
    "Mar",
    "Apr",
    "May",
    "Jun",
    "Jul",
    "Aug",
    "Sep",
    "Oct",
    "Nov",
    "Dec"
];
const cookies = parseCookie(document.cookie);
const token = cookies['auth_token'];
console.log("token: " + token);

function parseCookie(cookieString) {
    const cookies = {};
    cookieString.split(';').forEach(cookie => {
        const [name, value] = cookie.trim().split('=');
        cookies[name] = decodeURIComponent(value);
    });
    return cookies;
}

let allSpecies = [];
let inputElement = document.querySelector(".search input");

inputElement.addEventListener("input", onInputChange);

function getAllSpecies() {
    const url = window.location.origin + `/bad/api/images/main/filter/allspecies`;

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            console.log(data);
            allSpecies = data.map((species) => {
                console.log(species);
                return species;
            })
            if (wait) {
                wait = false;
                updateTab("");
            }
            distinctSpecies();
        })
}

function onInputChange() {
    removeAutocompleteDropdown();

    let value = inputElement.value.toLowerCase();
    if (value.length === 0) return;

    const filteredNames = [];

    allSpecies.forEach((SpeciesName) => {
        if (SpeciesName.substring(0, value.length).toLowerCase() === value) {
            filteredNames.push(SpeciesName);
        }
    })

    console.log(filteredNames)
    createAutocompleteDropdown(filteredNames);
}

function removeAutocompleteDropdown() {
    const listEl = document.querySelector("#autocomplete-list");
    if (listEl) listEl.remove(); //checks if it exists and then removes it
}

function createAutocompleteDropdown(list) {
    console.log("entered autocomplete");
    const listEl = document.createElement("ul");
    listEl.className = "autocomplete-list";
    listEl.id = "autocomplete-list";
    list.forEach((species) => {
        const listItem = document.createElement("li");

        const speciesButton = document.createElement("button");
        speciesButton.innerHTML = species;
        speciesButton.addEventListener("click", onSpeciesClick);
        speciesButton.addEventListener("keypress", function (event) {
            if (event.key === "Enter") {
                onSpeciesClick(event);
            }
        })
        listItem.appendChild(speciesButton);

        listEl.appendChild(listItem);
    });
    document.querySelector(".search").appendChild(listEl);
}


function onSpeciesClick(event) {
    console.log("species clicked");
    event.preventDefault();

    const buttonEl = event.target;
    inputElement.value = buttonEl.innerHTML.toLowerCase();

    removeAutocompleteDropdown();
    sendInput(event);
}


function sendInput(event) {
    console.log("input called");
    event.preventDefault();

    if (inputElement.value.length === 0) {
        location.reload();
    } else {
        const url = window.location.origin + `/bad/api/images/main/filter/species?species=${inputElement.value}`
        updateTab(url);
    }
}

function removeGallery() {
    location.reload();
}


function updateTab(url) {
    //TODO: Add stats creation


    // Fetching images
    if (url === "") {
        console.log("empty URL");
        let gallery = "";
        let i = 0;
        for (let index in allSpecies) {
            let url = window.location.origin + `/bad/api/images/main/filter/species?species=${allSpecies[index].toLowerCase()}`
            console.log("URL " + index + " : " + url);
            fetch(url, {
                headers: {
                    Authorization: token
                }
            }).then(response => {
                response.json().then(data => {
                    data.visits.forEach(visit => {
                        console.log("output from database: ")
                        console.log(visit);
                        if (i < 6) {
                            visit.images.forEach(image => {
                                if (i < 6) {
                                    gallery += `<div class="img"><img src="/bad/${image.image_path}" alt=""></div>`
                                    i++;
                                }
                            })
                        }
                    })
                    console.log("Gallery: " + gallery);
                    document.querySelector(".imgsection").innerHTML = gallery;
                });
            })
        }
    } else {
        console.log("non-empty URL");
        fetch(url, {
            headers: {
                Authorization: token
            }
        }).then(response => {
            response.json().then(data => {
                let i = 0;
                let gallery = "";
                data.visits.forEach(visit => {
                    console.log("output from database: ")
                    console.log(visit);
                    if (i < 7) {
                        visit.images.forEach(image => {
                            if (i < 7) {
                                gallery += `<div class="img"><img src="/bad/${image.image_path}" alt=""></div>`
                                i++;
                            }
                        })
                    }
                })
                console.log(gallery);
                document.querySelector(".imgsection").innerHTML = gallery;
            });
        })
    }


    // show only recent
    const recentURL = window.location.origin + `/bad/api/images/main/filter/recent?limit=3`;
    fetch(recentURL, {
        headers: {
            Authorization: token
        }
    }).then(response => {
        response.json().then(data => {
            let recent = "";
            console.log(data)
            let latitude;
            let longitude;
            navigator.geolocation.getCurrentPosition(function (position) {
                latitude = position.coords.latitude;
                longitude = position.coords.longitude;
                data.visits.forEach(visit => {
                    console.log("recent output: ");
                    console.log(visit);
                    let species = visit.species;
                    visit.images.forEach(image => {
                        const nominatimURL = `https://nominatim.openstreetmap.org/reverse?format=json&lat=${latitude}&lon=${longitude}`;
                        let day = new Date(image.date).getDate();
                        if (day < 10) day = "0" + day;


                        let date =
                            `${new Date(image.date).getFullYear()}-${new Date(image.date).getMonth() + 1}-${day}`;
                        console.log(date);
                        const weatherURL = `https://api.open-meteo.com/v1/forecast?latitude=${latitude}&longitude=${longitude}&daily=temperature_2m_max,temperature_2m_min,precipitation_sum&start_date=${date}&end_date=${date}`;
                        console.log("Weather Url: " + weatherURL);

                        fetch(nominatimURL)
                            .then(nominatim => nominatim.json())
                            .then(location => {
                                let address = "";
                                if (location.display_name) {
                                    address = location.display_name;
                                    console.log("Address: ", address)
                                } else console.error("Geocoding failed. Unable to retrieve address.");


                                fetch(weatherURL)
                                    .then(weather => weather.json())
                                    .then(weatherData => {

                                        const maxTemp = weatherData.daily.temperature_2m_max;
                                        const minTemp = weatherData.daily.temperature_2m_min;
                                        const precipitation = weatherData.daily.precipitation_sum;

                                        date = `${new Date(image.date).getDate()}
                                                    ${months[new Date(image.date).getMonth()]}
                                                    ${new Date(image.date).getFullYear()}`;


                                        recent +=
                                            `<div class="imgItem" xmlns="http://www.w3.org/1999/html">
                                            <img src="/bad/${image.image_path}" alt="">
                                            <span>${species}</span>
                                            <ul>
                                                <i class="fa-solid fa-calendar-days" style="color: #1c48b0; scale: 1.9; margin-right: 3vw"></i><span style="color: black; text-decoration: none; font-size: 18px">${date}</span></br>
                                                <i class="fa-solid fa-temperature-low" style="color: #c16c0b; scale: 1.9; margin-right: 3vw; padding-top: 2vh"></i><span style="color: black; text-decoration: none; font-size: 18px">Temperature: ${minTemp} - ${maxTemp}</span></br>
                                                <i class="fa-solid fa-cloud-showers-heavy" style="color: #148fad; scale: 1.9; margin-right: 3vw; padding-top: 2.8vh"></i><span style="color: black; text-decoration: none; font-size: 18px">Percipitation: ${precipitation}</span></br>
                                                <i class="fa-solid fa-map-location-dot" style="color: #c11551; scale: 1.9; margin-right: 3vw; padding-top: 3.1vh"></i><span style="color: black; text-decoration: none; font-size: 18px; margin-left: 4vw; display: flex; margin-top: -1vh">${address}</span></br> <!-- TODO: under the assumption that the Pi never moves --> 
                                            </ul>
                                        </div>`;

                                        const animation = `<div class="bird-container bird-container-one">
                                                                           <div class="bird bird-one"></div>
                                                                       </div>
                                                                       <div class="bird-container bird-container-two">
                                                                           <div class="bird bird-two"></div>
                                                                       </div>
                                                                       <div class="bird-container bird-container-three">
                                                                           <div class="bird bird-three"></div>
                                                                       </div>
                                                                       <div class="bird-container bird-container-four">
                                                                           <div class="bird bird-four"></div>
                                                                       </div>`;
                                        document.querySelector(".recent").innerHTML = `<h1 class="recTitle">Recent Captures</h1>` + recent + animation;
                                        console.log("recent: ", recent);
                                    })
                            })
                    })
                })
            })
        })
    })
}

//Separate search for the gallery view.

function showGallery() {
    document.querySelector(".original").style.display = "none";
    document.querySelector(".pureGallery").style.display = "block";
    document.querySelectorAll(".date label").forEach(label => label.style.color = "Black");
    document.querySelector("#search").value = "";

    let gallery = new Map();
    let url = window.location.origin + `/bad/api/images/main/filter/allspecies/images`
    console.log("URL : " + url);
    fetch(url, {
        headers: {
            Authorization: token
        }
    })
        .then(response => {
            response.json()
                .then(data => {
                    console.log(data);
                    data.visits.forEach(visit => {
                        console.log("visit : " + visit);
                        visit.images.forEach(image => {
                            console.log("image: ")
                            console.log(image);
                            const imageInfo = {
                                imagePath: image.image_path,
                                date: image.date
                            }
                            gallery.set(image.imageId, imageInfo);
                        })
                    })
                    displayGallery(gallery);
                })
        })
}

function displayGallery(gallery) {
    for (const [id, imageInfo] of gallery) {
        console.log("Gallery : " + id + ", " + imageInfo);

    }

    //sort and update gallery ([...gallery] spreads the map as an array)
    gallery = new Map([...gallery].sort(function (a, b) {
        console.log("inside sort: " + a);
        return new Date(a[1].date) - new Date(b[1].date);
    }));


    for (const [id, info] of gallery) {
        console.log(
            "Gallery sorted " + id + " : " +
            new Date(info.date).getDate() +
            " " +
            months[new Date(info.date).getMonth()] +
            " " +
            new Date(info.date).getFullYear());
    }

    let output = "";
    let previousDate = "";
    for (const [id, info] of gallery) {
        if (info.date === previousDate) {
            output += `<div class="img" onclick="openImage(${id})">
                            <img src="/bad/${info.imagePath}" alt="">
                       </div>`;
        } else {
            previousDate = info.date;
            output += `<h1 style="color: darkslateblue; text-decoration: underline">${new Date(info.date).getDate()} ${months[new Date(info.date).getMonth()]} ${new Date(info.date).getFullYear()}</h1>
                        <div class="img" onclick="openImage(${id})">
                            <img src="/bad/${info.imagePath}" alt="">
                        </div>`;
        }
    }
    document.querySelector(".pure_imgsection").innerHTML = output;
}

function openImage(id) {
    window.scrollTo({
        top: 0,
        behavior: 'instant'
    });
    const url = window.location.origin + `/bad/api/images/main/birds/${id}`;
    let child = "";
    let species;
    let date;
    console.log(url);
    fetch(url, {
        headers: {
            Authorization: token,
        }
    }).then(response => {
            response.json()
                .then(visit => {
                    console.log("visit : " + visit);
                    species = visit.species;
                    visit.images.forEach(image => {
                        console.log("image: ")
                        console.log(image);
                        child += `<div class="overlay">
                                        <div class="overlayNav"><span onclick="closeOverlay()" title="Close overlay"><i class="fa-solid fa-x" style="color: #ffffff;"></i></span></div>
                                        <div class="overlayImage"><img src="/bad/${image.image_path}" alt=""></div>
                                        <div class="overlayNav"><span onclick="trashImage(${id})" title="send to Archive"><i class="fa-regular fa-trash-can" style="color: #ffffff;"></i></span></div>
                                    </div>`;
                        date = `${new Date(image.date).getDate()} 
                                ${months[new Date(image.date).getMonth()]} 
                                ${new Date(image.date).getFullYear()}`;
                    })
                    document.querySelector(".pureGallery").innerHTML += child;
                    document.querySelector(".pureGallery .galTitle").innerHTML = `${species} - 
                            ${new Date(date).getDate()} ${months[new Date(date).getMonth()]} ${new Date(date).getFullYear()}`


                    //Change styles
                    let body = document.querySelector("body").style;
                    let gal = document.querySelector(".pureGallery").style;
                    let title = document.querySelector(".pureGallery .galTitle");
                    let nav = document.querySelector(".pureGallery .nav").style;
                    let imgSection = document.querySelector(".pure_imgsection").style;


                    body.overflow = "hidden";
                    body.background = "black";
                    gal.position = "relative";
                    gal.display = "block";
                    title.style.color = "white";
                    nav.display = "none";
                    imgSection.position = "absolute";
                    imgSection.opacity = "0.2";
                })
        }
    )
}

function closeOverlay() {
    //Change styles
    let body = document.querySelector("body").style;
    let gal = document.querySelector(".pureGallery").style;
    let title = document.querySelector(".pureGallery .galTitle");
    let nav = document.querySelector(".pureGallery .nav").style;
    let imgSection = document.querySelector(".pure_imgsection").style;


    body.overflow = "scroll";
    body.overflowX = "hidden";
    body.background = "white";
    gal.display = "block";
    title.style.color = "brown";
    nav.display = "flex";
    imgSection.position = "";
    imgSection.opacity = "1";

    document.querySelector(".overlay").remove();
    title.innerHTML = "Gallery";
}

function trashImage(id) {
    let url = window.location.origin + `/bad/api/images/main/birds/archive?ID=${id}`;

    //send image to the Archive Table
    fetch(url, {
        method: "PUT",
        headers: {
            Authorization: token
        }
    }).then(response => {
        if (response.ok) {
            response.text()
                .then(data => {
                    console.log(data);
                    url = window.location.origin + `/bad/api/images/main/birds/delete/${id}`;

                    fetch(url, {
                        method: "DELETE",
                        headers: {
                            Authorization: token
                        }
                    }).then(response => {
                        if (response.ok) response.text()
                            .then(res => {
                                console.log(res);
                                closeOverlay();
                                showGallery();
                            })
                    })
                })
        }
    })
}

//TODO: breaks after sending a bird to the archive
let galleryInput = document.getElementById("searchGallery")
galleryInput.addEventListener("input", inputChange)

function inputChange() {
    removeAutocompleteDropdown();
    const value = galleryInput.value.toLowerCase();

    if (value.length === 0) return;

    const filteredNames = [];
    console.log("all species: " + allSpecies);

    allSpecies.forEach((species) => {
        if (species.substring(0, value.length).toLowerCase() === value) {
            filteredNames.push(species);
        }
    })
    console.log("filtered names: " + filteredNames);
    createAutocompleteNew(filteredNames);
}

function createAutocompleteNew(list) {
    console.log("new autocomplete entered")
    const listEl = document.createElement("ul");
    listEl.className = "autocomplete-list";
    listEl.id = "autocomplete-list";
    list.forEach((species) => {
        const listItem = document.createElement("li");

        const speciesButton = document.createElement("button");
        speciesButton.innerHTML = species;
        speciesButton.addEventListener("click", onButtonClick);
        speciesButton.addEventListener("keypress", function (event) {
            if (event.key === "Enter") {
                onButtonClick(event);
            }
        })
        listItem.appendChild(speciesButton);

        listEl.appendChild(listItem);
    })
    document.querySelector("#search2").appendChild(listEl);
}

function onButtonClick(event) {
    console.log("button clicked")
    event.preventDefault(); //cancels default event (submitting the form)

    const buttonEl = event.target; //element that triggered the event (the button itself)
    inputElement.value = buttonEl.innerHTML; //should be a string of the booking name

    removeAutocompleteDropdown();
    sendNewInput(event);
}

function sendNewInput(event) {
    console.log("input called")
    event.preventDefault();

    if (inputElement.value.length === 0) {
        getAllSpecies();
        showGallery();
    } else {
        const url = window.location.origin + `/bad/api/images/main/filter/species?species=${inputElement.value}`
        updateGallery(url);
    }
}

function updateGallery(url) {
// Fetching images
    fetch(url, {
        headers: {
            Authorization: token
        }
    }).then(response => {
        response.json().then(data => {
            let gallery = new Map();
            console.log(data)
            data.visits.forEach(visit => {
                console.log(visit)
                visit.images.forEach(image => {
                    const imageInfo = {
                        imagePath: image.image_path,
                        date: image.date
                    }
                    gallery.set(image.imageId, imageInfo);
                })
            })
            console.log(gallery);
            displayGallery(gallery);
        });
    })
}

//Date filter
function filterByDate() {
    let start = document.getElementById("startDate").value;
    let end = document.getElementById("endDate").value;
    console.log("start: " + start);
    console.log("end: " + end);

    if (start.length === 0 || end.length === 0) {
        location.reload();
    } else {
        const url = window.location.origin + `/bad/api/images/main/filter/date?startDate=${start}&endDate=${end}`
        console.log("url: " + url);
        updateTab(url);
    }
}


function filterByDateGallery() {
    let start = document.getElementById("startDateGallery").value;
    let end = document.getElementById("endDateGallery").value;
    console.log("start: " + start);
    console.log("end: " + end);

    if (start.length === 0 || end.length === 0) {
        showGallery();
        console.log("reset gallery")
    } else {
        const url = window.location.origin + `/bad/api/images/main/filter/date?startDate=${start}&endDate=${end}`
        console.log("url: " + url);

        updateGallery(url);
    }
}

//TODO: doesnt work anymore if the user came from signup
function logout() {
    const url = window.location.origin + `/bad/api/accounts/logout?Authorization=${token}`;
    fetch(url, {
        method: 'POST',
        headers: {
            Authorization: `${token}`,
        },
    })
        .then(response => {
            if (response.ok) {
                response.text()
                    .then(res => {
                        console.log(res);
                        document.cookie = "auth_token =;  Path=/";
                        window.location.href = "./../index/H-Index.html";
                    })
            } else if (response.status === 304) {
                throw new Error("the given account was not logged in")
            } else {
                response.text().then(res => {
                    alert("Logout unsuccessful. \n" + res);
                })
            }
        })
        .catch(error => {
            console.error("Logout error:", error);
        });
}

//Archive
function Archive() {
    document.querySelector(".original").style.display = "none";
    document.querySelector(".archiveGallery").style.display = "block";
    document.querySelector(".archiveGallery button").style.display = "block"
    document.querySelector("#search").value = "";


    let gallery = new Map();
    let url = window.location.origin + `/bad/api/images/archive/filter/allspecies/images`
    console.log("URL : " + url);
    fetch(url, {
        headers: {
            Authorization: token
        }
    })
        .then(response => {
            response.json()
                .then(data => {
                    console.log(data);
                    data.visits.forEach(visit => {
                        console.log("visit : " + visit);
                        visit.images.forEach(image => {
                            console.log("image: ")
                            console.log(image);
                            const imageInfo = {
                                imagePath: image.image_path,
                                date: image.date
                            }
                            gallery.set(image.imageId, imageInfo);
                        })
                    })
                    displayArchive(gallery);
                })
        })
}

function displayArchive(gallery) {
    for (const [id, imageInfo] of gallery) {
        console.log("Archive : " + id + ", " + imageInfo);

    }

    //sort and update gallery ([...gallery] spreads the map as an array)
    gallery = new Map([...gallery].sort(function (a, b) {
        console.log("inside sort: " + a);
        return new Date(a[1].date) - new Date(b[1].date);
    }));


    for (const [id, info] of gallery) {
        console.log(
            "Archive sorted " + id + " : " +
            new Date(info.date).getDate() +
            " " +
            months[new Date(info.date).getMonth()] +
            " " +
            new Date(info.date).getFullYear());
    }

    let output = "";
    let previousDate = "";
    for (const [id, info] of gallery) {
        if (info.date === previousDate) {
            output += `<div class="img">
                            <img src="/bad/${info.imagePath}" alt="">
                            <span onclick="sendToGallery(${id})" class="toGallery" title="Send to Gallery">
                                <i class="fa-solid fa-arrow-up-from-bracket"></i>
                            </span> 
                            <span onclick="CompletelyTrashImage(${id})" class="delete" title="Permanently delete the image">
                                <i class="fa-regular fa-trash-can"></i>
                            </span>
                        </div>`;
        } else {
            previousDate = info.date;
            output += `<h1 style="color: darkslateblue; text-decoration: underline">${new Date(info.date).getDate()} ${months[new Date(info.date).getMonth()]} ${new Date(info.date).getFullYear()}</h1>
                        <div class="img">
                            <img src="/bad/${info.imagePath}" alt="">
                            <span onclick="sendToGallery(${id})" class="toGallery" title="Send to Gallery">
                                <i class="fa-solid fa-arrow-up-from-bracket"></i>
                            </span> 
                            <span onclick="CompletelyTrashImage(${id})" class="delete" title="Permanently delete the image">
                                <i class="fa-regular fa-trash-can"></i>
                            </span>
                        </div>`;
        }
    }
    document.querySelector(".archive_imgSection").innerHTML = output;
}

function sendToGallery(id) {
    let url = window.location.origin + `/bad/api/images/archive/birds/gallery?ID=${id}`;

    //send image to the Archive Table
    fetch(url, {
        method: "PUT",
        headers: {
            Authorization: token
        }
    }).then(response => {
        if (response.ok) {
            response.text()
                .then(data => {
                    console.log(data);
                    url = window.location.origin + `/bad/api/images/archive/birds/delete/${id}`;

                    fetch(url, {
                        method: "DELETE",
                        headers: {
                            Authorization: token
                        }
                    }).then(response => {
                        if (response.ok) response.text()
                            .then(res => {
                                console.log(res);
                                Archive();
                            })
                    })
                })
        }
    })
}

function CompletelyTrashImage(id) {
    const url = window.location.origin + `/bad/api/images/archive/birds/delete/${id}`;

    fetch(url, {
        method: "DELETE",
        headers: {
            Authorization: token
        }
    }).then(response => {
        if (response.ok) response.text()
            .then(res => {
                console.log(res);
                Archive();
            })
    })
}


function distinctSpecies() {
    document.querySelector("#distinct").innerHTML = (allSpecies.length - 1) + ""; //To account for the No Bird "species"
    console.log("ALL SPECIES: " + allSpecies);
    averageConfidence();
}

<!-- TODO: need to update statistics -->
function averageConfidence() {
    const url = window.location.origin + "/bad/api/images/main/visits"

    fetch(url, {
        method: "GET",
        headers: {
            Authorization: token
        }
    })
        .then(response => response.text())
        .then(confidence => {
            document.querySelector("#confidence").innerHTML = confidence;
            console.log("Confidence: " + confidence);
        })
}

let wait = true;
getAllSpecies();

