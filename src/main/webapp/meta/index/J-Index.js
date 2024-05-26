function portfolio() {
    const url = window.location.origin + `/bad/api/images/main/filter/recent?limit=6`;
    let portfolio = "";

    fetch(url)
        .then(response => {
            response.json()
                .then(data => {
                    data.visits.forEach(visit => {
                        console.log("output from database: ")
                        console.log(visit);

                        visit.images.forEach(image => {
                            portfolio += `<div class="galleryImage">
                                                <img src="/bad/${image.image_path}" alt="">
                                            </div>`;
                        })
                    })
                    document.querySelector(".gallery").innerHTML = portfolio;
                })
        })
}

portfolio();