package com.berti.notes.controller;

import java.util.List;

import com.berti.notes.model.Notes;
import com.berti.notes.repository.NotesRepository;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotesController {

    @Autowired
    NotesRepository noteRepo;

    @GetMapping("/api/notes")
    @ResponseBody
    public List<Notes> getAll(@RequestParam(name = "query", required = false) String search,
            @RequestParam(name = "delete", required = false) String id) {
        if (id != null) {
            int id2 = Double.valueOf(id).intValue();
            noteRepo.deleteItem(id2);
        }
        if (search != null) {
            return noteRepo.getSearch(search);
        } else {
            return noteRepo.getAllNotes();
        }
    }

    @PostMapping(value = "/api/notes", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Notes newNote(@RequestBody String payload) {
        int added = 0;
        int updatedID = 0;
        Notes rs;

        try {
            JSONParser parser = new JSONParser();
            Object object = parser.parse(payload);
            JSONObject jsonObject = (JSONObject) object;

            int i = jsonObject.get("id") != null ? Double.valueOf(jsonObject.get("id").toString()).intValue() : 0;
            String title = (String) jsonObject.get("title");
            String content = (String) jsonObject.get("content");
            if (i > 0) {
                updatedID = i;
                noteRepo.updateNote(i, title, content);
            } else {
                added = noteRepo.addNote(title, content);
            }
        } catch (Exception e) {
            return new Notes(null, "ERROR", "The note id " + updatedID + " does not exist.");
        }
        if (added == 0) {
            System.out.println("added:" + added);
            System.out.println("id:" + updatedID);
            rs = noteRepo.getNote(updatedID);
        } else {
            rs = noteRepo.lastNote();
        }
        return new Notes(rs.getId(), rs.getTitle(), rs.getContent());

    }

    @GetMapping("/api/notes/{id}")
    public Notes getNoteById(@PathVariable(value = "id") int noteId) {
        try {
            return noteRepo.getNote(noteId);
        } catch (Exception e) {
            return new Notes(null, "ERROR", "The note id " + noteId + " does not exist.");
        }

    }

        /**
         * This is hacky, but when you delete a note, the list of notes is returned whether it succeeds or fails 
         * Technically, frontend could check for length to identify/resolve an error in deletion (non-existing note)
         * Orginally I was returning a string here with success or error - but thats not very REST-y
         */
    @GetMapping("/api/notes/delete")
    @ResponseBody
    public List<Notes> deleteItem(@RequestParam("itemId") int itemId) {

        try {
            if (noteRepo.deleteItem(itemId) >= 1) {
                return noteRepo.getAllNotes();
            }
        } catch (Exception e) {
            return noteRepo.getAllNotes();
        }

        return noteRepo.getAllNotes();
    }

}