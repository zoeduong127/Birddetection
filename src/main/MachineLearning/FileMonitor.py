import os
from datetime import datetime
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler
from BirdSpeciesClassification import isBird
from CallToDatabase import mainImage, archiveImage

# Specify the directory to watch and the database file
date = datetime.today().strftime('%Y-%m-%d')
watched_folder = "/var/lib/motioneye/Camera1/" + date
new_folder = "/var/lib/motioneye/Camera1/images/" + date

# Define a custom event handler for file system events
class MyHandler(FileSystemEventHandler):
    def on_created(self, event):
        if event.is_directory:
            return
        # Get the file name and path of the newly created file
        filename = os.path.basename(event.src_path)
        path = os.path.dirname(event.src_path)

        # run isBird on the newly created file
        bird,species,confidence=isBird(path + '/' + filename)
        
        if bird:
            print("The image contains a bird!")
            mainImage(filepath=new_folder + '/' + filename, species=species, confidence=confidence)
        else:
            print("The image does not contain a bird!")
            archiveImage(filepath=new_folder + '/' + filename)

# Set up the file system observer
event_handler = MyHandler()
observer = Observer()
observer.schedule(event_handler, path=watched_folder, recursive=False)
observer.start()

try:
    # Keep the program running
    while True:
        pass
except KeyboardInterrupt:
    # Stop the observer when the user interrupts the program
    observer.stop()

# Wait until the observer threads finish execution
observer.join()