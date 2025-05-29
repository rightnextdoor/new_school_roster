// src/components/profile/ProfileLoadingSections.tsx
import React from 'react';
import './profile.css';

const studentSections = [
  { id: 'user-info', title: 'User Info' },
  { id: 'contact-info', title: 'Contact Info' },
  { id: 'parent-family', title: 'Parent & Family' },
  { id: 'nutritional', title: 'Nutritional' },
  { id: 'academic', title: 'Academic' },
  { id: 'history', title: 'School History' },
  { id: 'account-settings', title: 'Account Settings' },
];

const nonStudentSections = [
  { id: 'user-info', title: 'User Info' },
  { id: 'contact-doe', title: 'Contact & Department of Education' },
  { id: 'civil-family', title: 'Civil Status & Family' },
  { id: 'dependents', title: 'Dependent Children' },
  { id: 'career', title: 'Career & Appointments' },
  { id: 'education', title: 'Educational Background' },
  { id: 'gov-ids', title: 'Government IDs' },
  { id: 'account-settings', title: 'Account Settings' },
];

interface Props {
  isStudent: boolean;
}

export default function ProfileLoadingSections({ isStudent }: Props) {
  const sections = isStudent ? studentSections : nonStudentSections;

  return (
    <>
      {sections.map(({ id, title }) => (
        <section key={id} id={id} className="card">
          <header>{title}</header>
          <div className="content">
            <div className="spinner" />
          </div>
        </section>
      ))}
    </>
  );
}
