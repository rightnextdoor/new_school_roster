// Represents one row in the “All Students” list
export interface StudentListItem {
  /** the user’s account ID */
  user: {
    id: number;
    email: string;
    roles: string[];
  };

  /** the profile’s ID, or null if no profile exists yet */
  profileId: number | null;

  /** the student’s full name (first, middle, last) or null if no profile */
  fullName: string | null;

  /** URL to the student’s photo, or null if none */
  photoUrl: string | null;
}
