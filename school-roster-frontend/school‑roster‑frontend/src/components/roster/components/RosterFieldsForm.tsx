import React from 'react';
import '../styles/roster-update.css';

interface RosterFieldsFormProps {
  subjectName: string;
  onSubjectNameChange: (val: string) => void;
  period: string;
  onPeriodChange: (val: string) => void;
  nickname: string;
  onNicknameChange: (val: string) => void;
  gradeLevel: string;
  onGradeLevelChange: (val: string) => void;
}

export default function RosterFieldsForm({
  subjectName,
  onSubjectNameChange,
  period,
  onPeriodChange,
  nickname,
  onNicknameChange,
  gradeLevel,
  onGradeLevelChange,
}: RosterFieldsFormProps) {
  return (
    <div className="form-container">
      <div className="form-group">
        <label htmlFor="subjectName">Subject Name</label>
        <input
          type="text"
          id="subjectName"
          value={subjectName}
          onChange={(e) => onSubjectNameChange(e.target.value)}
        />
      </div>

      <div className="form-group">
        <label htmlFor="period">Period</label>
        <input
          type="text"
          id="period"
          value={period}
          onChange={(e) => onPeriodChange(e.target.value)}
        />
      </div>

      <div className="form-group">
        <label htmlFor="nickname">Section / Nickname</label>
        <input
          type="text"
          id="nickname"
          value={nickname}
          onChange={(e) => onNicknameChange(e.target.value)}
        />
      </div>

      <div className="form-group">
        <label htmlFor="gradeLevel">Grade Level</label>
        <input
          type="text"
          id="gradeLevel"
          value={gradeLevel}
          onChange={(e) => onGradeLevelChange(e.target.value)}
        />
      </div>
    </div>
  );
}
