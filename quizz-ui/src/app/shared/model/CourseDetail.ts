export class CourseDetail {
  constructor(
    public courseId: number = 0,
    public courseCode: string = '',
    public subjects: SubjectCourse[] = [],
  ) {
  }
}


export class SubjectCourse {
  constructor(
    public subjectId: number = 0,
    public subjectName: string = '',
    public subjectIcon: string = '',
    public isExpanded: boolean = false,
    public exams: ExamCourse[] = [],
  ) {
  }
}

export class ExamCourse {
  constructor(
    public testId: number = 0,
    public name: string = '',
    public startDate: string = '',
    public endDate: string = '',
    public time: number = 0,
    public cssClass: string = '',
    public status: string = '',
    public isJoined: boolean = false,
  ) {
  }
}
