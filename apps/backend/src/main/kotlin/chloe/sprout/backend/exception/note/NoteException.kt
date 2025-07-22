package chloe.sprout.backend.exception.note

import chloe.sprout.backend.exception.CustomException

class NoteNotFoundException() : CustomException(NoteErrorCode.NOTE_NOT_FOUND)
class NoteTitleRequiredException() : CustomException(NoteErrorCode.NOTE_TITLE_REQUIRED)
class NoteOwnerMismatchException() : CustomException(NoteErrorCode.NOTE_OWNER_MISMATCH)