// src/components/students/StudentsListPage.tsx

import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getStudentList } from '../../services/profileApi';
import type { StudentListItem } from './types';
import './students.css';

export default function StudentsListPage() {
  const [students, setStudents] = useState<StudentListItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getStudentList()
      .then(setStudents)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    if (!loading) {
      const scrollY = sessionStorage.getItem('studentsScroll');
      if (scrollY) {
        window.scrollTo(0, parseInt(scrollY, 10));
        sessionStorage.removeItem('studentsScroll');
      }
    }
  }, [loading]);

  if (loading) {
    return (
      <div className="students-container">
        <p>Loading...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="students-container">
        <p>Error: {error}</p>
      </div>
    );
  }

  return (
    <div className="students-container">
      <section className="students-card">
        <header>All Students</header>
        <div className="content">
          {students.length === 0 ? (
            <p>No students found.</p>
          ) : (
            <table className="students-table">
              <thead>
                <tr>
                  <th>Photo</th>
                  <th>User ID</th>
                  <th>Full Name</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {students.map((item) => (
                  <tr key={item.user.id}>
                    <td>
                      {item.photoUrl ? (
                        <img
                          src={item.photoUrl}
                          alt={`${item.fullName} photo`}
                          className="avatar-sm"
                        />
                      ) : (
                        <div className="avatar-sm avatar-placeholder" />
                      )}
                    </td>
                    <td>{item.user.id}</td>
                    <td>{item.fullName}</td>
                    <td>
                      {item.profileId ? (
                        <>
                          <Link
                            to={`/profile/${item.user.id}`}
                            state={{ from: '/students' }}
                            className="students-link"
                            onClick={() =>
                              sessionStorage.setItem(
                                'studentsScroll',
                                String(window.scrollY)
                              )
                            }
                          >
                            View Profile
                          </Link>
                          <Link
                            to={`/profile/${item.user.id}/edit`}
                            state={{ from: '/students' }}
                            className="students-link"
                            onClick={() =>
                              sessionStorage.setItem(
                                'studentsScroll',
                                String(window.scrollY)
                              )
                            }
                          >
                            Edit Profile
                          </Link>
                        </>
                      ) : (
                        <Link
                          to={`/profile/${item.user.id}/edit`}
                          state={{ from: '/students' }}
                          className="students-link"
                          onClick={() =>
                            sessionStorage.setItem(
                              'studentsScroll',
                              String(window.scrollY)
                            )
                          }
                        >
                          Create Profile
                        </Link>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </section>
    </div>
  );
}
